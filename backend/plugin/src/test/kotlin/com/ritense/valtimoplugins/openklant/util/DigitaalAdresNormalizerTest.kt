package com.ritense.valtimoplugins.openklant.util

import com.ritense.valtimoplugins.openklant.model.SoortDigitaalAdres
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

internal class DigitaalAdresNormalizerTest {
    @ParameterizedTest
    @ValueSource(
        strings = [
            "06 12 34 56 78",
            "0612345678",
            "06-12345678",
            "+31 6 1234 5678",
            "+31(0)612345678",
            "0031612345678",
            "612345678",
            "6 12 34 56 78",
        ],
    )
    fun `recognises one Dutch mobile number however it is written`(telefoonnummer: String) {
        assertEquals("0612345678", DigitaalAdresNormalizer.normalize(SoortDigitaalAdres.TELEFOONNUMMER, telefoonnummer))
    }

    @ParameterizedTest
    @ValueSource(strings = ["+32 470 12 34 56", "0032470123456", "0032-470.12.34.56"])
    fun `writes a foreign number with the plus of E164`(telefoonnummer: String) {
        val genormaliseerd = DigitaalAdresNormalizer.normalize(SoortDigitaalAdres.TELEFOONNUMMER, telefoonnummer)
        assertEquals("+32470123456", genormaliseerd)
    }

    @Test
    fun `keeps a foreign number of 15 digits within what Open Klant accepts`() {
        // Open Klant accepts 9 to 15 digits after +, but only 7 to 13 after 00
        assertEquals(
            "+491234567890123",
            DigitaalAdresNormalizer.normalize(SoortDigitaalAdres.TELEFOONNUMMER, "0049 1234 567890123"),
        )
    }

    @Test
    fun `writes a foreign number shorter than 9 digits with 00, as Open Klant only accepts it that way`() {
        assertEquals("006834002", DigitaalAdresNormalizer.normalize(SoortDigitaalAdres.TELEFOONNUMMER, "+683 4002"))
        assertEquals("006834002", DigitaalAdresNormalizer.normalize(SoortDigitaalAdres.TELEFOONNUMMER, "006834002"))
    }

    @Test
    fun `adds the leading 0 only to a number that can only be a Dutch mobile number`() {
        // Eight digits, or nine starting with something else, are no Dutch mobile number: Open Klant decides on those
        assertEquals("61234567", DigitaalAdresNormalizer.normalize(SoortDigitaalAdres.TELEFOONNUMMER, "61234567"))
        assertEquals("512345678", DigitaalAdresNormalizer.normalize(SoortDigitaalAdres.TELEFOONNUMMER, "512345678"))
    }

    @ParameterizedTest
    @ValueSource(strings = ["Jan.Jansen@Voorbeeld.nl", " jan.jansen@voorbeeld.nl ", "JAN.JANSEN@VOORBEELD.NL"])
    fun `compares e-mail addresses without case and surrounding spaces`(email: String) {
        assertEquals("jan.jansen@voorbeeld.nl", DigitaalAdresNormalizer.normalize(SoortDigitaalAdres.EMAIL, email))
    }

    @Test
    fun `keeps a plus address distinct from the address without it`() {
        assertNotEquals(
            DigitaalAdresNormalizer.normalize(SoortDigitaalAdres.EMAIL, "jan+test@voorbeeld.nl"),
            DigitaalAdresNormalizer.normalize(SoortDigitaalAdres.EMAIL, "jan@voorbeeld.nl"),
        )
    }

    @Test
    fun `only trims an overig adres`() {
        assertEquals("Postbus 12", DigitaalAdresNormalizer.normalize(SoortDigitaalAdres.OVERIG, " Postbus 12 "))
    }
}
