package com.ritense.valtimoplugins.openklant.service

import com.ritense.valtimoplugins.openklant.BaseIntegrationTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.datasource.DataSourceTransactionManager
import org.springframework.transaction.support.TransactionTemplate
import java.time.Duration
import java.util.UUID
import java.util.concurrent.Callable
import java.util.concurrent.CountDownLatch
import java.util.concurrent.ExecutionException
import java.util.concurrent.Executors
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicLong
import javax.sql.DataSource

/** Runs against the PostgreSQL database of the integration tests. */
class DatabaseDigitaalAdresLockIT : BaseIntegrationTest() {
    @Autowired
    lateinit var dataSource: DataSource

    private val executor = Executors.newFixedThreadPool(2)

    @AfterEach
    fun stopThreads() {
        executor.shutdownNow()
    }

    private fun <T> async(block: () -> T): Future<T> = executor.submit(Callable { block() })

    @Test
    fun `lets only one caller at a time in for the same key, outside a transaction`() {
        val lock = DatabaseDigitaalAdresLock(dataSource)
        val key = "${UUID.randomUUID()}:email:jan@voorbeeld.nl"
        val inside = AtomicInteger()
        val highest = AtomicInteger()
        val start = CountDownLatch(1)

        val futures =
            (1..2).map {
                async {
                    start.await()
                    lock.withLock(key) {
                        highest.accumulateAndGet(inside.incrementAndGet()) { a, b -> maxOf(a, b) }
                        Thread.sleep(500)
                        inside.decrementAndGet()
                    }
                }
            }
        start.countDown()
        futures.forEach { it.get(30, TimeUnit.SECONDS) }

        assertEquals(1, highest.get())
    }

    @Test
    fun `holds the lock until the surrounding transaction ends`() {
        val lock = DatabaseDigitaalAdresLock(dataSource)
        val key = "${UUID.randomUUID()}:telefoonnummer:0612345678"
        val transaction = TransactionTemplate(DataSourceTransactionManager(dataSource))
        val locked = CountDownLatch(1)
        val committedAt = AtomicLong()
        val secondInAt = AtomicLong()

        val first =
            async {
                transaction.executeWithoutResult {
                    lock.withLock(key) { locked.countDown() }
                    // Still in the transaction: the lock must still be held
                    Thread.sleep(1000)
                    committedAt.set(System.nanoTime())
                }
            }
        locked.await(30, TimeUnit.SECONDS)
        val second = async { lock.withLock(key) { secondInAt.set(System.nanoTime()) } }
        first.get(30, TimeUnit.SECONDS)
        second.get(30, TimeUnit.SECONDS)

        assertTrue(secondInAt.get() >= committedAt.get(), "the second caller got in before the transaction ended")
    }

    @Test
    fun `gives up after the timeout, so the job can be retried`() {
        val key = "${UUID.randomUUID()}:email:wacht@voorbeeld.nl"
        val holding = CountDownLatch(1)
        val release = CountDownLatch(1)
        val holder =
            async {
                DatabaseDigitaalAdresLock(dataSource).withLock(key) {
                    holding.countDown()
                    release.await(30, TimeUnit.SECONDS)
                }
            }
        holding.await(30, TimeUnit.SECONDS)
        try {
            val impatient = DatabaseDigitaalAdresLock(dataSource, timeout = Duration.ofMillis(500))
            val waiter = async { impatient.withLock(key) { } }

            val exception = assertThrows<ExecutionException> { waiter.get(30, TimeUnit.SECONDS) }
            assertTrue(exception.cause is IllegalStateException)
        } finally {
            release.countDown()
            holder.get(30, TimeUnit.SECONDS)
        }
    }

    @Test
    fun `leaves the lock_timeout of the surrounding transaction as it was`() {
        val lock = DatabaseDigitaalAdresLock(dataSource)
        val jdbc = JdbcTemplate(dataSource)
        val transaction = TransactionTemplate(DataSourceTransactionManager(dataSource))

        val after =
            transaction.execute {
                jdbc.queryForObject("select set_config('lock_timeout', '7s', true)", String::class.java)
                lock.withLock("${UUID.randomUUID()}:email:tijd@voorbeeld.nl") { }
                jdbc.queryForObject("select current_setting('lock_timeout')", String::class.java)
            }

        assertEquals("7s", after)
    }

    @Test
    fun `lets PostgreSQL end a deadlock instead of waiting for the timeout`() {
        val lock = DatabaseDigitaalAdresLock(dataSource)
        val transaction = TransactionTemplate(DataSourceTransactionManager(dataSource))
        val eerste = "${UUID.randomUUID()}:email:een@voorbeeld.nl"
        val tweede = "${UUID.randomUUID()}:email:twee@voorbeeld.nl"
        val bothHoldOne = CountDownLatch(2)

        fun lockBoth(
            first: String,
            second: String,
        ) = async {
            transaction.executeWithoutResult {
                lock.withLock(first) { }
                bothHoldOne.countDown()
                bothHoldOne.await(30, TimeUnit.SECONDS)
                lock.withLock(second) { }
            }
        }

        val started = System.nanoTime()
        val futures = listOf(lockBoth(eerste, tweede), lockBoth(tweede, eerste))
        val results = futures.map { runCatching { it.get(30, TimeUnit.SECONDS) } }
        val seconds = Duration.ofNanos(System.nanoTime() - started).seconds

        val failures = results.mapNotNull { it.exceptionOrNull()?.cause }
        assertEquals(1, failures.size, "exactly one of the two transactions should be ended: $failures")
        assertTrue(failures.single().message!!.contains("deadlock"), failures.single().message)
        // deadlock_timeout is 1 s by default; the lock timeout of 60 s must not have been reached
        assertTrue(seconds < 30, "took $seconds s")
    }

    @Test
    fun `releases the lock when the block fails`() {
        val lock = DatabaseDigitaalAdresLock(dataSource)
        val key = "${UUID.randomUUID()}:overig:Postbus 12"

        assertThrows<IllegalArgumentException> { lock.withLock(key) { throw IllegalArgumentException("fout") } }

        val impatient = DatabaseDigitaalAdresLock(dataSource, timeout = Duration.ofMillis(500))
        assertEquals("vrij", impatient.withLock(key) { "vrij" })
    }
}
