package com.ritense.valtimoplugins.openklant.model

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class OpenKlantQueryTest {
    @Test
    fun `fromKeyValueQueryParamList trims and keeps the configured order`() {
        val query =
            OpenKlantQuery.fromKeyValueQueryParamList(
                listOf(
                    KeyValueQueryParam(" soortPartij ", " persoon "),
                    KeyValueQueryParam("pageSize", "50"),
                ),
            )

        assertEquals(mapOf("soortPartij" to "persoon", "pageSize" to "50"), query.queryParams)
        assertEquals(listOf("soortPartij", "pageSize"), query.queryParams.keys.toList())
    }

    @Test
    fun `fromKeyValueQueryParamList skips pairs with a blank key or value`() {
        val query =
            OpenKlantQuery.fromKeyValueQueryParamList(
                listOf(
                    KeyValueQueryParam("", "persoon"),
                    KeyValueQueryParam("nummer", "  "),
                    KeyValueQueryParam("indicatieActief", "true"),
                ),
            )

        assertEquals(mapOf("indicatieActief" to "true"), query.queryParams)
    }

    @Test
    fun `of skips null values`() {
        val query = OpenKlantQuery.of("iban" to "NL02ABNA0123456789", "bic" to null)

        assertEquals(mapOf("iban" to "NL02ABNA0123456789"), query.queryParams)
    }

    @Test
    fun `add rejects a duplicate filter key`() {
        val query = OpenKlantQuery.of("nummer" to "1")

        val exception = assertThrows<IllegalArgumentException> { query.add("nummer", "2") }

        assertEquals("Duplicate filter key: 'nummer'", exception.message)
    }

    @Test
    fun `DigitaalAdresQuery shares the same parameter handling`() {
        val query =
            DigitaalAdresQuery.fromKeyValueQueryParamList(
                listOf(
                    KeyValueQueryParam(" soortDigitaalAdres ", " email "),
                    KeyValueQueryParam("", ""),
                ),
            )

        assertEquals(mapOf("soortDigitaalAdres" to "email"), query.queryParams)
        assertThrows<IllegalArgumentException> { query.add("soortDigitaalAdres", "telefoonnummer") }
    }
}
