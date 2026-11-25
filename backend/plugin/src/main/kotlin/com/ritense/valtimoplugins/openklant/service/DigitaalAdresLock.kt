package com.ritense.valtimoplugins.openklant.service

/**
 * Lets only one process at a time look up and create the same digitaal adres. Open Klant has no unique constraint
 * on the adres itself, so two processes that look it up at the same moment would otherwise both create it.
 */
interface DigitaalAdresLock {
    fun <T> withLock(
        key: String,
        block: () -> T,
    ): T
}
