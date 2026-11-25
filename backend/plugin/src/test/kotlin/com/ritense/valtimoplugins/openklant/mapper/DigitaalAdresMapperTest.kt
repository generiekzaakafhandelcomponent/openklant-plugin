package com.ritense.valtimoplugins.openklant.mapper

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.ritense.valtimoplugins.openklant.model.DigitaalAdres
import com.ritense.valtimoplugins.openklant.model.SoortDigitaalAdres
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.UUID

internal class DigitaalAdresMapperTest {
    // Like an application that leaves out null values by default
    private val objectMapper =
        ObjectMapper().registerKotlinModule().setDefaultPropertyInclusion(JsonInclude.Include.NON_NULL)
    private val partij = UUID.fromString("3e3e0001-0000-4000-8000-000000000001")
    private val betrokkene = UUID.fromString("3e3e0002-0000-4000-8000-000000000002")

    @Test
    fun `an adres of only a betrokkene is sent with verstrektDoorPartij null`() {
        val request = adres(betrokkene = betrokkene).toCreationRequest()

        assertNull(request.verstrektDoorPartij)
        assertEquals(betrokkene, request.verstrektDoorBetrokkene?.uuid)
        val json = objectMapper.valueToTree<JsonNode>(request)
        // Open Klant 2.19 answers 400 when the key is missing
        assertTrue(json.has("verstrektDoorPartij"), json.toString())
        assertTrue(json.get("verstrektDoorPartij").isNull, json.toString())
        assertEquals(betrokkene.toString(), json.path("verstrektDoorBetrokkene").path("uuid").asText())
    }

    @Test
    fun `an adres of a partij keeps working as before`() {
        val request = adres(partij = partij).toCreationRequest()

        assertEquals(partij, request.verstrektDoorPartij?.uuid)
        assertNull(request.verstrektDoorBetrokkene)
    }

    @Test
    fun `an adres needs a partij or a betrokkene`() {
        val exception = assertThrows<IllegalArgumentException> { adres().toCreationRequest() }

        assertEquals("A digitaal adres needs verstrektDoorPartij, verstrektDoorBetrokkene or both", exception.message)
    }

    @Test
    fun `isStandaardAdres needs a partij, as Open Klant rejects it otherwise`() {
        assertThrows<IllegalArgumentException> {
            adres(betrokkene = betrokkene, isStandaardAdres = true).toCreationRequest()
        }
    }

    private fun adres(
        partij: UUID? = null,
        betrokkene: UUID? = null,
        isStandaardAdres: Boolean? = null,
    ) = DigitaalAdres(
        verstrektDoorPartijUuid = partij,
        verstrektDoorBetrokkeneUuid = betrokkene,
        adres = "jan@example.com",
        soortDigitaalAdres = SoortDigitaalAdres.EMAIL,
        isStandaardAdres = isStandaardAdres,
    )
}
