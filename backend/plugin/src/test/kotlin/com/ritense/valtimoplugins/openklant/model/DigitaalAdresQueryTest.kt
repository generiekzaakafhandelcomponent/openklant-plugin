package com.ritense.valtimoplugins.openklant.model

import org.junit.jupiter.api.Assertions.*
import kotlin.test.Test

class DigitaalAdresQueryTest {
    @Test
    fun `adds a valid paramName-value pair to map`() {
        val query = DigitaalAdresQuery()

        query.add("bsn", "123456789")

        assertEquals(query.queryParams.size, 1)
        assertEquals(query.queryParams["bsn"], "123456789")
    }

    @Test
    fun `ignores a paramName-value pair with a null paramName`() {
        val query = DigitaalAdresQuery()

        query.add(null, "123456789")

        assertTrue(query.queryParams.isEmpty())
    }

    @Test
    fun `ignores a paramName-value pair with an empty paramName`() {
        val query = DigitaalAdresQuery()

        query.add("", "hello@example.com")

        assertTrue(query.queryParams.isEmpty())
    }

    @Test
    fun `ignores a paramName-value pair with a null value`() {
        val query = DigitaalAdresQuery()

        query.add("bsn", null)

        assertTrue(query.queryParams.isEmpty())
    }

    @Test
    fun `ignores a paramName-value pair with an empty value`() {
        val query = DigitaalAdresQuery()

        query.add("email", "")

        assertTrue(query.queryParams.isEmpty())
    }

    @Test
    fun `throws exception when duplicate paramNames are added`() {
        val query = DigitaalAdresQuery()

        query.add("bsn", "123456789")

        assertThrows(IllegalArgumentException::class.java) {
            query.add("bsn", "987654321")
        }
    }
}