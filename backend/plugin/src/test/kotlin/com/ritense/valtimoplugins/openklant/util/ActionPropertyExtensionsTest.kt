package com.ritense.valtimoplugins.openklant.util

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.UUID

internal class ActionPropertyExtensionsTest {
    @Test
    fun `toRequiredUuid accepts a padded uuid`() {
        assertEquals(UUID.fromString(UUID_A), "  $UUID_A  ".toRequiredUuid("uuid"))
    }

    @Test
    fun `toRequiredUuid rejects a blank value`() {
        listOf(null, "", "   ").forEach { value ->
            val exception = assertThrows<IllegalArgumentException> { value.toRequiredUuid("partijUuid") }
            assertEquals("Action property 'partijUuid' is required and must contain a UUID", exception.message)
        }
    }

    @Test
    fun `toNestedUuidIfPresent returns null for a blank value`() {
        assertNull("".toNestedUuidIfPresent())
        assertNull(null.toNestedUuidIfPresent())
        assertEquals(UUID.fromString(UUID_A), UUID_A.toNestedUuidIfPresent()?.uuid)
    }

    @Test
    fun `toNestedUuidList splits on commas and ignores blanks`() {
        assertEquals(
            listOf(UUID.fromString(UUID_A), UUID.fromString(UUID_B)),
            " $UUID_A , , $UUID_B ".toNestedUuidList()?.map { it.uuid },
        )
    }

    @Test
    fun `toNestedUuidList returns null when nothing was configured`() {
        assertNull(null.toNestedUuidList())
        assertNull("   ".toNestedUuidList())
    }

    @Test
    fun `toNestedUuidList rejects a malformed uuid rather than silently dropping it`() {
        assertThrows<IllegalArgumentException> { "$UUID_A,not-a-uuid".toNestedUuidList() }
    }

    @Test
    fun `toRequiredString trims and rejects blanks`() {
        assertEquals("Klachten", "  Klachten  ".toRequiredString("naam"))

        val exception = assertThrows<IllegalArgumentException> { "  ".toRequiredString("naam") }
        assertEquals("Action property 'naam' is required", exception.message)
    }

    private companion object {
        const val UUID_A = "4f4f0001-0000-4000-8000-000000000001"
        const val UUID_B = "4f4f0002-0000-4000-8000-000000000002"
    }
}
