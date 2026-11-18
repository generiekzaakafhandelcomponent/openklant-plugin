package com.ritense.valtimoplugins.openklant.service

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.jdbc.datasource.DataSourceUtils
import java.nio.ByteBuffer
import java.security.MessageDigest
import java.sql.Connection
import java.sql.SQLException
import java.time.Duration
import javax.sql.DataSource

/**
 * A PostgreSQL advisory lock in the GZAC database, so the lock holds across every GZAC instance that uses it.
 *
 * In a process step the connection is already in a transaction (Operaton runs each command in a Spring transaction,
 * and Spring binds its JDBC connection). The lock is then taken with `pg_advisory_xact_lock` on that connection, and
 * PostgreSQL releases it when the transaction ends: on a commit, a rollback or a lost connection, so a lock can never
 * be left behind. Outside a transaction the lock gets a short transaction of its own around the block.
 *
 * Waiting is left to PostgreSQL, so its deadlock detection applies: when two transactions each wait for a lock the
 * other holds, PostgreSQL ends one of them after `deadlock_timeout`. The wait is limited to [timeout] through a
 * `lock_timeout` that only applies while waiting. A process that gives up fails its job, which Operaton retries.
 *
 * On another database this falls back to [LocalDigitaalAdresLock], which only covers one GZAC instance.
 */
class DatabaseDigitaalAdresLock(
    private val dataSource: DataSource,
    private val timeout: Duration = Duration.ofSeconds(60),
) : DigitaalAdresLock {
    private val fallback = LocalDigitaalAdresLock()

    @Volatile
    private var postgres: Boolean? = null

    init {
        require(!timeout.isNegative && !timeout.isZero) { "The lock timeout must be positive, but was $timeout" }
    }

    override fun <T> withLock(
        key: String,
        block: () -> T,
    ): T {
        if (!isPostgres()) {
            return fallback.withLock(key, block)
        }
        val connection = DataSourceUtils.getConnection(dataSource)
        try {
            if (DataSourceUtils.isConnectionTransactional(connection, dataSource) && !connection.autoCommit) {
                logger.debug { "Locking '$key' in the GZAC database until the surrounding transaction ends" }
                acquire(connection, key)
                return block()
            }
            logger.debug { "Locking '$key' in the GZAC database, in a transaction of its own" }
            return inOwnTransaction(connection) {
                acquire(connection, key)
                block()
            }
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource)
        }
    }

    private fun <T> inOwnTransaction(
        connection: Connection,
        block: () -> T,
    ): T {
        val autoCommit = connection.autoCommit
        connection.autoCommit = false
        try {
            return block()
        } finally {
            // Ends the transaction and so releases the lock; nothing was written in it
            connection.rollback()
            connection.autoCommit = autoCommit
        }
    }

    private fun acquire(
        connection: Connection,
        key: String,
    ) {
        // set_config with is_local = true only lasts until the transaction ends, also when waiting fails
        val previousTimeout = query(connection, "select current_setting('lock_timeout')")
        query(connection, "select set_config('lock_timeout', ?, true)", "${timeout.toMillis().coerceAtLeast(1)}ms")
        try {
            connection.prepareStatement("select pg_advisory_xact_lock(?)").use { statement ->
                statement.setLong(1, lockId(key))
                statement.executeQuery().close()
            }
        } catch (e: SQLException) {
            throw lockFailure(e)
        }
        // The rest of the surrounding transaction keeps the lock_timeout it had
        query(connection, "select set_config('lock_timeout', ?, true)", previousTimeout)
    }

    private fun lockFailure(e: SQLException): Exception {
        val reason =
            when (e.sqlState) {
                LOCK_NOT_AVAILABLE -> "another process has been creating the same digitaal adres for more than $timeout"
                DEADLOCK_DETECTED -> "deadlock, this transaction holds a lock that the process it waits for wants"
                else -> return e
            }
        return IllegalStateException("Could not lock the digitaal adres: $reason", e)
    }

    private fun query(
        connection: Connection,
        sql: String,
        vararg parameters: String,
    ): String =
        connection.prepareStatement(sql).use { statement ->
            parameters.forEachIndexed { index, value -> statement.setString(index + 1, value) }
            statement.executeQuery().use { result ->
                check(result.next()) { "No result for: $sql" }
                result.getString(1)
            }
        }

    private fun isPostgres(): Boolean {
        postgres?.let { return it }
        val connection = DataSourceUtils.getConnection(dataSource)
        val isPostgres =
            try {
                connection.metaData.databaseProductName.equals("PostgreSQL", ignoreCase = true)
            } finally {
                DataSourceUtils.releaseConnection(connection, dataSource)
            }
        if (!isPostgres) {
            logger.warn {
                "The GZAC database is not PostgreSQL: get-or-create-digitaal-adres only locks within this GZAC instance"
            }
        }
        postgres = isPostgres
        return isPostgres
    }

    companion object {
        private const val NAMESPACE = "openklant-plugin:digitaal-adres"

        // SQLSTATEs of PostgreSQL: lock_not_available (lock_timeout) and deadlock_detected
        private const val LOCK_NOT_AVAILABLE = "55P03"
        private const val DEADLOCK_DETECTED = "40P01"

        private val logger = KotlinLogging.logger { }

        /** A 64-bit key for PostgreSQL; a collision only makes two unrelated processes wait for each other. */
        fun lockId(key: String): Long =
            ByteBuffer.wrap(MessageDigest.getInstance("SHA-256").digest("$NAMESPACE:$key".toByteArray())).long
    }
}
