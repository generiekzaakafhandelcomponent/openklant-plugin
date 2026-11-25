package com.ritense.valtimoplugins.openklant.model

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class KlantcontactQueryTest {
    @Test
    fun `maps every field to its Open Klant filter in a fixed order`() {
        val query =
            KlantcontactQuery(
                objectTypeId = "zaak",
                objectUuid = "uuid789",
                bsn = "123456789",
                partijUuid = "partij1",
            ).toOpenKlantQuery()

        assertEquals(
            listOf(
                "onderwerpobject__onderwerpobjectidentificatorCodeObjecttype" to "zaak",
                "hadBetrokkene__wasPartij__partijIdentificator__objectId" to "123456789",
                "onderwerpobject__onderwerpobjectidentificatorObjectId" to "uuid789",
                "hadBetrokkene__wasPartij__uuid" to "partij1",
            ),
            query.queryParams.toList(),
        )
    }

    @Test
    fun `leaves out null and blank fields`() {
        val query = KlantcontactQuery(objectTypeId = " ", bsn = "123456789").toOpenKlantQuery()

        assertEquals(mapOf("hadBetrokkene__wasPartij__partijIdentificator__objectId" to "123456789"), query.queryParams)
    }

    @Test
    fun `gives an empty query when nothing is filled in`() {
        assertTrue(KlantcontactQuery().toOpenKlantQuery().queryParams.isEmpty())
    }
}
