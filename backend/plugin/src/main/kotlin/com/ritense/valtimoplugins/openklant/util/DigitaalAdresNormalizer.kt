package com.ritense.valtimoplugins.openklant.util

import com.ritense.valtimoplugins.openklant.model.SoortDigitaalAdres

/**
 * Brings a digitaal adres into a comparable form, so the same adres written differently is still recognised.
 * For a telefoonnummer the result is also a form Open Klant accepts (no separators); e-mail is only compared this way.
 */
object DigitaalAdresNormalizer {
    private val PHONE_SEPARATORS = Regex("[\\s\\-./()]")
    private val DUTCH_COUNTRY_CODE = Regex("^(\\+31|0031)0?")
    private val INTERNATIONAL = Regex("^(\\+|00)([0-9]+)$")

    // Open Klant accepts +, followed by 9 to 15 digits (E.164), or 00, followed by 7 to 13 digits. A foreign
    // number is written with + so the longest numbers fit; only one shorter than 9 digits needs 00.
    private const val MIN_DIGITS_AFTER_PLUS = 9

    // A Dutch mobile number without its leading 0, as people often type it; NotifyNL reads it the same way
    private val MOBILE_WITHOUT_ZERO = Regex("^6[0-9]{8}$")

    fun normalize(
        soort: SoortDigitaalAdres,
        adres: String,
    ): String =
        when (soort) {
            // The local part is case-sensitive in theory, but no mail provider treats it that way
            SoortDigitaalAdres.EMAIL -> adres.trim().lowercase()
            // 06 12 34 56 78, 0612345678, 612345678, +31 6 1234 5678, +31(0)612345678 and 0031612345678 are one
            // number; a foreign number gets the + of E.164
            SoortDigitaalAdres.TELEFOONNUMMER ->
                adres
                    .replace(PHONE_SEPARATORS, "")
                    .replace(DUTCH_COUNTRY_CODE, "0")
                    .let { if (MOBILE_WITHOUT_ZERO.matches(it)) "0$it" else it }
                    .let { international(it) }
            SoortDigitaalAdres.OVERIG -> adres.trim()
        }

    /** +32 470 12 34 56 and 0032470123456 both become +32470123456. */
    private fun international(nummer: String): String {
        val digits = INTERNATIONAL.matchEntire(nummer)?.groupValues?.get(2) ?: return nummer
        return if (digits.length >= MIN_DIGITS_AFTER_PLUS) "+$digits" else "00$digits"
    }
}
