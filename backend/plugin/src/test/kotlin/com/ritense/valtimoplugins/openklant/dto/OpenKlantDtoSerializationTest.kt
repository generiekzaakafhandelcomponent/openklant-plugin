package com.ritense.valtimoplugins.openklant.dto

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.ritense.valtimoplugins.openklant.model.NestedUuid
import com.ritense.valtimoplugins.openklant.model.SoortDigitaalAdres
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.UUID

internal class OpenKlantDtoSerializationTest {
    private val objectMapper = ObjectMapper().registerKotlinModule()

    // Actor: the API discriminates 'actorIdentificatie' on the sibling 'soortActor' property

    @Test
    fun `reads the medewerker variant of actorIdentificatie`() {
        val actor =
            objectMapper.readValue<Actor>(
                """
                {"uuid":"$UUID_A","url":"http://ok/actoren/$UUID_A","naam":"Jamie","soortActor":"medewerker",
                 "indicatieActief":true,
                 "actorIdentificatie":{"functie":"Balie","emailadres":"balie@example.com",
                                       "telefoonnummer":"0612345678"}}
                """.trimIndent(),
            )

        assertEquals(Actor.SoortActor.MEDEWERKER, actor.soortActor)
        assertEquals("Balie", actor.actorIdentificatie?.functie)
        assertEquals("balie@example.com", actor.actorIdentificatie?.emailadres)
        assertEquals("0612345678", actor.actorIdentificatie?.telefoonnummer)
        assertNull(actor.actorIdentificatie?.faxnummer)
    }

    @Test
    fun `reads the geautomatiseerde actor variant of actorIdentificatie`() {
        val actor =
            objectMapper.readValue<Actor>(
                """
                {"uuid":"$UUID_A","url":"http://ok/actoren/$UUID_A","naam":"Robot",
                 "soortActor":"geautomatiseerde_actor",
                 "actorIdentificatie":{"functie":"Chatbot","omschrijving":"Beantwoordt standaardvragen"}}
                """.trimIndent(),
            )

        assertEquals(Actor.SoortActor.GEAUTOMATISEERDE_ACTOR, actor.soortActor)
        assertEquals("Chatbot", actor.actorIdentificatie?.functie)
        assertEquals("Beantwoordt standaardvragen", actor.actorIdentificatie?.omschrijving)
        assertNull(actor.actorIdentificatie?.telefoonnummer)
    }

    @Test
    fun `reads the organisatorische eenheid variant of actorIdentificatie`() {
        val actor =
            objectMapper.readValue<Actor>(
                """
                {"uuid":"$UUID_A","url":"http://ok/actoren/$UUID_A","naam":"Klantcontactcentrum",
                 "soortActor":"organisatorische_eenheid",
                 "actorIdentificatie":{"omschrijving":"KCC","emailadres":"kcc@example.com",
                                       "faxnummer":"0201234567","telefoonnummer":"14020"}}
                """.trimIndent(),
            )

        assertEquals(Actor.SoortActor.ORGANISATORISCHE_EENHEID, actor.soortActor)
        assertEquals("KCC", actor.actorIdentificatie?.omschrijving)
        assertEquals("0201234567", actor.actorIdentificatie?.faxnummer)
        assertNull(actor.actorIdentificatie?.functie)
    }

    @Test
    fun `reads an actor without actorIdentificatie`() {
        val actor =
            objectMapper.readValue<Actor>(
                """
                {"uuid":"$UUID_A","url":"http://ok/actoren/$UUID_A","naam":"Jamie","soortActor":"medewerker",
                 "actorIdentificatie":null,"actoridentificator":null}
                """.trimIndent(),
            )

        assertNull(actor.actorIdentificatie)
        assertNull(actor.actoridentificator)
    }

    @Test
    fun `writes actorIdentificatie without the fields that do not apply`() {
        val json =
            objectMapper.writeValueAsString(
                Actor.ActorIdentificatie(functie = "Balie", telefoonnummer = "0612345678"),
            )

        assertEquals("""{"functie":"Balie","telefoonnummer":"0612345678"}""", json)
    }

    // Enums

    @Test
    fun `serializes enums using their API value`() {
        assertEquals("\"medewerker\"", objectMapper.writeValueAsString(Actor.SoortActor.MEDEWERKER))
        assertEquals(
            "\"geautomatiseerde_actor\"",
            objectMapper.writeValueAsString(Actor.SoortActor.GEAUTOMATISEERDE_ACTOR),
        )
        assertEquals("\"klant\"", objectMapper.writeValueAsString(Betrokkene.Rol.KLANT))
        assertEquals("\"te_verwerken\"", objectMapper.writeValueAsString(InterneTaakStatus.TE_VERWERKEN))
        assertEquals("\"contactpersoon\"", objectMapper.writeValueAsString(Partij.SoortPartij.CONTACTPERSOON))
        assertEquals("\"telefoonnummer\"", objectMapper.writeValueAsString(SoortDigitaalAdres.TELEFOONNUMMER))
    }

    @Test
    fun `parses enum values case insensitively and trims surrounding whitespace`() {
        assertEquals(Actor.SoortActor.MEDEWERKER, Actor.SoortActor.fromValue(" Medewerker "))
        assertEquals(Betrokkene.Rol.VERTEGENWOORDIGER, Betrokkene.Rol.fromValue("VERTEGENWOORDIGER"))
        assertEquals(InterneTaakStatus.VERWERKT, InterneTaakStatus.fromValue("Verwerkt"))
        assertEquals(Partij.SoortPartij.PERSOON, Partij.SoortPartij.fromValue("persoon"))
        assertEquals(SoortDigitaalAdres.EMAIL, SoortDigitaalAdres.fromValue("EMAIL"))
    }

    @Test
    fun `rejects an unknown enum value with a message listing the supported values`() {
        val exception = assertThrows<IllegalArgumentException> { InterneTaakStatus.fromValue("afgerond") }

        assertEquals(
            "Unknown interne taak status 'afgerond'. Supported values: te_verwerken, verwerkt",
            exception.message,
        )
    }

    // Request bodies

    @Test
    fun `patch requests only carry the fields that were set`() {
        assertEquals("{}", objectMapper.writeValueAsString(PatchActorRequest()))
        assertEquals("{}", objectMapper.writeValueAsString(PatchBetrokkeneRequest()))
        assertEquals("{}", objectMapper.writeValueAsString(PatchKlantcontactRequest()))
        assertEquals("{}", objectMapper.writeValueAsString(PatchPartijRequest()))
        assertEquals("{}", objectMapper.writeValueAsString(PatchVertegenwoordigingRequest()))
        assertEquals(
            """{"iban":"NL02ABNA0123456789"}""",
            objectMapper.writeValueAsString(PatchRekeningnummerRequest(iban = "NL02ABNA0123456789")),
        )
    }

    @Test
    fun `reads a partij whose categorieRelaties carry a categorie name`() {
        val partij =
            objectMapper.readValue<Partij>(
                """
                {"uuid":"$UUID_A","url":"http://ok/partijen/$UUID_A","nummer":"1","interneNotitie":"",
                 "betrokkenen":[],
                 "categorieRelaties":[{"uuid":"$UUID_B","url":"http://ok/categorie-relaties/$UUID_B",
                                       "categorieNaam":"Klachten","beginDatum":"2026-01-01","eindDatum":null}],
                 "digitaleAdressen":[],"voorkeursDigitaalAdres":null,"vertegenwoordigden":[],
                 "rekeningnummers":[],"voorkeursRekeningnummer":null,"partijIdentificatoren":[],
                 "soortPartij":"persoon","indicatieGeheimhouding":false,"voorkeurstaal":"nld",
                 "indicatieActief":true,"bezoekadres":null,"correspondentieadres":null,
                 "partijIdentificatie":null}
                """.trimIndent(),
            )

        // The API returns categorieNaam as a plain string, not as a nested object.
        assertEquals("Klachten", partij.categorieRelaties.single().categorieNaam)
        assertEquals("2026-01-01", partij.categorieRelaties.single().beginDatum)
    }

    @Test
    fun `nested references are written as an object holding the uuid`() {
        val json =
            objectMapper.writeValueAsString(
                CreateVertegenwoordigingRequest(
                    vertegenwoordigendePartij = NestedUuid(UUID.fromString(UUID_A)),
                    vertegenwoordigdePartij = NestedUuid(UUID.fromString(UUID_B)),
                ),
            )

        assertEquals(
            """{"vertegenwoordigendePartij":{"uuid":"$UUID_A"},"vertegenwoordigdePartij":{"uuid":"$UUID_B"}}""",
            json,
        )
    }

    @Test
    fun `klantcontact request bodies never carry the fields that only exist from klantinteracties 0 8 0`() {
        val createFields =
            objectMapper.readTree(
                objectMapper.writeValueAsString(
                    KlantcontactCreationRequest.KlantcontactRequest(
                        kanaal = "telefoon",
                        onderwerp = "Vraag",
                        taal = "nld",
                        vertrouwelijk = false,
                    ),
                ),
            )
        val patchFields = objectMapper.readTree(objectMapper.writeValueAsString(PatchKlantcontactRequest()))

        listOf(createFields, patchFields).forEach { node ->
            assertNull(node.get("hoofdOnderwerpType"))
            assertNull(node.get("verdereActieOndernomen"))
        }
    }

    @Test
    fun `klantcontact responses expose the 0 8 0 fields when the instance returns them`() {
        val klantcontact =
            objectMapper.readValue<Klantcontact>(
                """
                {"uuid":"$UUID_A","url":"http://ok/klantcontacten/$UUID_A","gingOverOnderwerpobjecten":[],
                 "hadBetrokkenActoren":[],"omvatteBijlagen":[],"hadBetrokkenen":[],"leiddeTotInterneTaken":[],
                 "nummer":"1","referentienummer":null,"kanaal":"telefoon","onderwerp":"Vraag","inhoud":null,
                 "reactie":null,"indicatieContactGelukt":null,"hoofdOnderwerpType":"http://ok/types/1",
                 "verdereActieOndernomen":true,"taal":"nld","vertrouwelijk":false,"plaatsgevondenOp":null,
                 "metadata":{},"_expand":null}
                """.trimIndent(),
            )

        assertEquals("http://ok/types/1", klantcontact.hoofdOnderwerpType)
        assertEquals(true, klantcontact.verdereActieOndernomen)
    }

    private companion object {
        const val UUID_A = "2d2d0001-0000-4000-8000-000000000001"
        const val UUID_B = "2d2d0002-0000-4000-8000-000000000002"
    }
}
