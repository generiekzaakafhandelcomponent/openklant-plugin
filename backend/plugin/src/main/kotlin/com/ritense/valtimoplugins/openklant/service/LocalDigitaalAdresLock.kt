package com.ritense.valtimoplugins.openklant.service

import java.util.concurrent.locks.ReentrantLock

/** A lock within this JVM only. It covers one GZAC instance; [DatabaseDigitaalAdresLock] covers all of them. */
class LocalDigitaalAdresLock : DigitaalAdresLock {
    private val stripes = Array(STRIPES) { ReentrantLock() }

    override fun <T> withLock(
        key: String,
        block: () -> T,
    ): T {
        val lock = stripes[Math.floorMod(key.hashCode(), STRIPES)]
        lock.lock()
        try {
            return block()
        } finally {
            lock.unlock()
        }
    }

    companion object {
        private const val STRIPES = 64
    }
}
