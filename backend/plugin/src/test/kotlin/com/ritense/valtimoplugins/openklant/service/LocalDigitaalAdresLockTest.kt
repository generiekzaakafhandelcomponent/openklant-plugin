package com.ritense.valtimoplugins.openklant.service

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.util.concurrent.Callable
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

class LocalDigitaalAdresLockTest {
    private val lock = LocalDigitaalAdresLock()

    @Test
    fun `lets only one caller at a time in for the same key`() {
        assertEquals(1, highestConcurrency("partij:email:jan@voorbeeld.nl", "partij:email:jan@voorbeeld.nl"))
    }

    @Test
    fun `lets callers with different keys in at the same time`() {
        // Keys on different stripes, so nothing has to wait
        val keys = (0 until 1000).map { "key-$it" }
        val first = keys.first()
        val other = keys.first { Math.floorMod(it.hashCode(), 64) != Math.floorMod(first.hashCode(), 64) }

        assertEquals(2, highestConcurrency(first, other))
    }

    private fun highestConcurrency(
        firstKey: String,
        secondKey: String,
    ): Int {
        val inside = AtomicInteger()
        val highest = AtomicInteger()
        val start = CountDownLatch(1)
        val executor = Executors.newFixedThreadPool(2)
        val enter =
            { key: String ->
                Callable {
                    start.await()
                    lock.withLock(key) {
                        highest.accumulateAndGet(inside.incrementAndGet()) { a, b -> maxOf(a, b) }
                        Thread.sleep(300)
                        inside.decrementAndGet()
                    }
                }
            }
        try {
            val futures = listOf(firstKey, secondKey).map { executor.submit(enter(it)) }
            start.countDown()
            futures.forEach { it.get(10, TimeUnit.SECONDS) }
        } finally {
            executor.shutdownNow()
        }
        return highest.get()
    }
}
