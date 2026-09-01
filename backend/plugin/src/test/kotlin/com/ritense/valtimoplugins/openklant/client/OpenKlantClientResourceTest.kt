package com.ritense.valtimoplugins.openklant.client

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.ritense.valtimoplugins.openklant.dto.Actor
import com.ritense.valtimoplugins.openklant.dto.Betrokkene
import com.ritense.valtimoplugins.openklant.dto.CreateActorKlantcontactRequest
import com.ritense.valtimoplugins.openklant.dto.CreateActorRequest
import com.ritense.valtimoplugins.openklant.dto.CreateBetrokkeneRequest
import com.ritense.valtimoplugins.openklant.dto.CreateBijlageRequest
import com.ritense.valtimoplugins.openklant.dto.CreateInterneTaakRequest
import com.ritense.valtimoplugins.openklant.dto.CreateOnderwerpobjectRequest
import com.ritense.valtimoplugins.openklant.dto.CreatePartijIdentificatorRequest
import com.ritense.valtimoplugins.openklant.dto.CreateRekeningnummerRequest
import com.ritense.valtimoplugins.openklant.dto.CreateVertegenwoordigingRequest
import com.ritense.valtimoplugins.openklant.dto.Identificator
import com.ritense.valtimoplugins.openklant.dto.InterneTaakStatus
import com.ritense.valtimoplugins.openklant.dto.Onderwerpobjectidentificator
import com.ritense.valtimoplugins.openklant.dto.PatchInterneTaakRequest
import com.ritense.valtimoplugins.openklant.dto.PatchRekeningnummerRequest
import com.ritense.valtimoplugins.openklant.model.NestedUuid
import com.ritense.valtimoplugins.openklant.model.OpenKlantProperties
import com.ritense.valtimoplugins.openklant.model.OpenKlantQuery
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.client.RestClient
import org.springframework.web.server.ResponseStatusException
import java.util.UUID

/** Exercises every klantinteracties resource against a real HTTP server, asserting request and response. */
internal class OpenKlantClientResourceTest {
    private lateinit var server: MockWebServer
    private lateinit var client: OpenKlantClient
    private lateinit var properties: OpenKlantProperties

    private val objectMapper = ObjectMapper().registerKotlinModule()

    @BeforeEach
    fun setUp() {
        server = MockWebServer()
        server.start()
        client = OpenKlantClient(RestClient.builder())
        properties =
            OpenKlantProperties(
                klantinteractiesUrl = server.url("/klantinteracties/api/v1/").toUri(),
                token = "test-token",
            )
    }

    @AfterEach
    fun tearDown() {
        server.shutdown()
    }

    // Cross-cutting behaviour

    @Test
    fun `sends the configured token on every request`() {
        enqueuePage()

        client.getRekeningnummers(OpenKlantQuery(), properties)

        assertEquals("Token test-token", server.takeRequest().getHeader(HttpHeaders.AUTHORIZATION))
    }

    @Test
    fun `follows pagination until the last page`() {
        val first = server.url("/klantinteracties/api/v1/rekeningnummers?page=2").toString()
        enqueueBody(
            """{"count":3,"next":"$first","previous":null,"results":[${rekeningnummerJson("Een")}]}""",
        )
        enqueueBody(
            """{"count":3,"next":null,"previous":null,"results":[${rekeningnummerJson(
                "Twee",
            )},${rekeningnummerJson("Drie")}]}""",
        )

        val rekeningnummers = client.getRekeningnummers(OpenKlantQuery(), properties)

        assertEquals(listOf("Een", "Twee", "Drie"), rekeningnummers.map { it.iban })
        assertEquals("/klantinteracties/api/v1/rekeningnummers?page=1", server.takeRequest().path)
        assertEquals("/klantinteracties/api/v1/rekeningnummers?page=2", server.takeRequest().path)
    }

    @Test
    fun `does not paginate when the caller pinned a page`() {
        enqueueBody(
            """{"count":3,"next":"http://irrelevant/next","previous":null,"results":[${rekeningnummerJson("Een")}]}""",
        )

        val rekeningnummers = client.getRekeningnummers(OpenKlantQuery.of("page" to "2", "pageSize" to "1"), properties)

        assertEquals(listOf("Een"), rekeningnummers.map { it.iban })
        assertEquals(1, server.requestCount)
        val path = server.takeRequest().path!!
        assertTrue(path.contains("page=2"), path)
        assertTrue(path.contains("pageSize=1"), path)
    }

    @Test
    fun `passes arbitrary filters through as query parameters`() {
        enqueuePage()

        client.getBetrokkenen(
            OpenKlantQuery.of(
                "wasPartij__uuid" to "8b0e0f0f-0000-4000-8000-000000000000",
                "organisatienaam" to "Ritense BV",
            ),
            properties,
        )

        val path = server.takeRequest().path!!
        assertTrue(path.contains("wasPartij__uuid=8b0e0f0f-0000-4000-8000-000000000000"), path)
        assertTrue(path.contains("organisatienaam=Ritense%20BV") || path.contains("organisatienaam=Ritense+BV"), path)
    }

    @Test
    fun `translates a client error into a ResponseStatusException carrying the status`() {
        server.enqueue(MockResponse().setResponseCode(400).setBody("""{"detail":"invalid"}"""))

        val exception =
            assertThrows<ResponseStatusException> {
                client.getRekeningnummer(UUID.randomUUID(), properties)
            }

        assertEquals(HttpStatus.BAD_REQUEST, exception.statusCode)
    }

    @Test
    fun `translates a server error into a 500 ResponseStatusException`() {
        server.enqueue(MockResponse().setResponseCode(500).setBody("boom"))

        val exception =
            assertThrows<ResponseStatusException> {
                client.getRekeningnummer(UUID.randomUUID(), properties)
            }

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.statusCode)
    }

    // Actoren

    @Test
    fun `getActoren lists actoren`() {
        enqueueBody("""{"count":1,"next":null,"previous":null,"results":[$MEDEWERKER_ACTOR_JSON]}""")

        val actoren = client.getActoren(OpenKlantQuery.of("soortActor" to "medewerker"), properties)

        assertEquals(1, actoren.size)
        assertEquals(Actor.SoortActor.MEDEWERKER, actoren.single().soortActor)
        assertEquals("Balie", actoren.single().actorIdentificatie?.functie)
        val path = server.takeRequest().path!!
        assertTrue(path.startsWith("/klantinteracties/api/v1/actoren?"), path)
        assertTrue(path.contains("soortActor=medewerker"), path)
    }

    @Test
    fun `getActor retrieves a single actor`() {
        enqueueBody(MEDEWERKER_ACTOR_JSON)
        val uuid = UUID.fromString(ACTOR_UUID)

        val actor = client.getActor(uuid, properties)

        assertEquals("Jamie de Vries", actor.naam)
        val request = server.takeRequest()
        assertEquals("GET", request.method)
        assertEquals("/klantinteracties/api/v1/actoren/$uuid", request.path)
    }

    @Test
    fun `createActor posts the actor and flattens actorIdentificatie`() {
        enqueueBody(MEDEWERKER_ACTOR_JSON)

        client.createActor(
            CreateActorRequest(
                naam = "Jamie de Vries",
                soortActor = Actor.SoortActor.MEDEWERKER,
                indicatieActief = true,
                actorIdentificatie = Actor.ActorIdentificatie(functie = "Balie", telefoonnummer = "0612345678"),
            ),
            properties,
        )

        val request = server.takeRequest()
        assertEquals("POST", request.method)
        assertEquals("/klantinteracties/api/v1/actoren", request.path)
        val body = bodyOf(request)
        assertEquals("medewerker", body.path("soortActor").asText())
        assertEquals("Balie", body.path("actorIdentificatie").path("functie").asText())
        assertEquals("0612345678", body.path("actorIdentificatie").path("telefoonnummer").asText())
        assertTrue(body.path("actorIdentificatie").path("faxnummer").isMissingNode)
        assertTrue(body.path("actoridentificator").isMissingNode)
    }

    @Test
    fun `deleteActor uses DELETE and tolerates an empty response`() {
        server.enqueue(MockResponse().setResponseCode(204))
        val uuid = UUID.fromString(ACTOR_UUID)

        client.deleteActor(uuid, properties)

        val request = server.takeRequest()
        assertEquals("DELETE", request.method)
        assertEquals("/klantinteracties/api/v1/actoren/$uuid", request.path)
    }

    // Actorklantcontacten

    @Test
    fun `createActorKlantcontact links an actor to a klantcontact`() {
        enqueueBody(
            """
            {"uuid":"$ACTOR_KLANTCONTACT_UUID","url":"http://ok/actorklantcontacten/$ACTOR_KLANTCONTACT_UUID",
             "actor":{"uuid":"$ACTOR_UUID","url":"http://ok/actoren/$ACTOR_UUID"},
             "klantcontact":{"uuid":"$KLANTCONTACT_UUID","url":"http://ok/klantcontacten/$KLANTCONTACT_UUID"}}
            """.trimIndent(),
        )

        val created =
            client.createActorKlantcontact(
                CreateActorKlantcontactRequest(
                    actor = NestedUuid(UUID.fromString(ACTOR_UUID)),
                    klantcontact = NestedUuid(UUID.fromString(KLANTCONTACT_UUID)),
                ),
                properties,
            )

        assertEquals(UUID.fromString(ACTOR_UUID), created.actor.uuid)
        val body = bodyOf(server.takeRequest())
        assertEquals(ACTOR_UUID, body.path("actor").path("uuid").asText())
        assertEquals(KLANTCONTACT_UUID, body.path("klantcontact").path("uuid").asText())
    }

    // Betrokkenen

    @Test
    fun `createBetrokkene omits absent optional fields`() {
        enqueueBody(BETROKKENE_JSON)

        val betrokkene =
            client.createBetrokkene(
                CreateBetrokkeneRequest(
                    hadKlantcontact = NestedUuid(UUID.fromString(KLANTCONTACT_UUID)),
                    rol = Betrokkene.Rol.KLANT,
                    initiator = true,
                ),
                properties,
            )

        assertEquals(Betrokkene.Rol.KLANT, betrokkene.rol)
        val body = bodyOf(server.takeRequest())
        assertEquals("klant", body.path("rol").asText())
        assertTrue(body.path("initiator").asBoolean())
        assertTrue(body.path("wasPartij").isMissingNode)
        assertTrue(body.path("contactnaam").isMissingNode)
    }

    // Bijlagen

    @Test
    fun `createBijlage registers a document reference`() {
        enqueueBody(
            """
            {"uuid":"$BIJLAGE_UUID","url":"http://ok/bijlagen/$BIJLAGE_UUID",
             "wasBijlageVanKlantcontact":{"uuid":"$KLANTCONTACT_UUID","url":"http://ok/klantcontacten/$KLANTCONTACT_UUID"},
             "bijlageidentificator":{"objectId":"doc-1","codeObjecttype":"DOCUMENT","codeRegister":"open-zaak",
                                     "codeSoortObjectId":"uuid"}}
            """.trimIndent(),
        )

        val bijlage =
            client.createBijlage(
                CreateBijlageRequest(
                    wasBijlageVanKlantcontact = NestedUuid(UUID.fromString(KLANTCONTACT_UUID)),
                    bijlageidentificator =
                        com.ritense.valtimoplugins.openklant.dto.BijlageIdentificator(
                            objectId = "doc-1",
                            codeObjecttype = "DOCUMENT",
                            codeRegister = "open-zaak",
                            codeSoortObjectId = "uuid",
                        ),
                ),
                properties,
            )

        assertEquals("doc-1", bijlage.bijlageidentificator?.objectId)
        assertEquals("/klantinteracties/api/v1/bijlagen", server.takeRequest().path)
    }

    // Interne taken

    @Test
    fun `createInterneTaak posts the status as its API value`() {
        enqueueBody(INTERNE_TAAK_JSON)

        val taak =
            client.createInterneTaak(
                CreateInterneTaakRequest(
                    gevraagdeHandeling = "Terugbellen",
                    aanleidinggevendKlantcontact = NestedUuid(UUID.fromString(KLANTCONTACT_UUID)),
                    status = InterneTaakStatus.TE_VERWERKEN,
                    toegewezenAanActoren = listOf(NestedUuid(UUID.fromString(ACTOR_UUID))),
                ),
                properties,
            )

        assertEquals(InterneTaakStatus.TE_VERWERKEN, taak.status)
        assertEquals(listOf(UUID.fromString(ACTOR_UUID)), taak.toegewezenAanActoren.map { it.uuid })
        val body = bodyOf(server.takeRequest())
        assertEquals("te_verwerken", body.path("status").asText())
        assertEquals(
            ACTOR_UUID,
            body
                .path("toegewezenAanActoren")
                .first()
                .path("uuid")
                .asText(),
        )
    }

    @Test
    fun `patchInterneTaak can close a task without touching other fields`() {
        enqueueBody(INTERNE_TAAK_JSON)
        val uuid = UUID.fromString(INTERNE_TAAK_UUID)

        client.patchInterneTaak(
            uuid,
            PatchInterneTaakRequest(status = InterneTaakStatus.VERWERKT, afgehandeldOp = "2026-02-01T10:00:00Z"),
            properties,
        )

        val request = server.takeRequest()
        assertEquals("PATCH", request.method)
        assertEquals(
            """{"status":"verwerkt","afgehandeldOp":"2026-02-01T10:00:00Z"}""",
            request.body.readUtf8(),
        )
    }

    // Klantcontacten

    @Test
    fun `searchKlantcontacten reads the 0 9 0 only fields as null on older instances`() {
        enqueueBody("""{"count":1,"next":null,"previous":null,"results":[$KLANTCONTACT_JSON]}""")

        val klantcontacten = client.searchKlantcontacten(OpenKlantQuery.of("kanaal" to "telefoon"), properties)

        assertNull(klantcontacten.single().hoofdOnderwerpType)
        assertNull(klantcontacten.single().verdereActieOndernomen)
    }

    // Onderwerpobjecten

    @Test
    fun `createOnderwerpobject links a klantcontact to a zaak`() {
        enqueueBody(
            """
            {"uuid":"$ONDERWERPOBJECT_UUID","url":"http://ok/onderwerpobjecten/$ONDERWERPOBJECT_UUID",
             "klantcontact":{"uuid":"$KLANTCONTACT_UUID","url":"http://ok/klantcontacten/$KLANTCONTACT_UUID"},
             "wasKlantcontact":null,
             "onderwerpobjectidentificator":{"objectId":"zaak-1","codeObjecttype":"zaak","codeRegister":"open-zaak",
                                             "codeSoortObjectId":"uuid"}}
            """.trimIndent(),
        )

        val onderwerpobject =
            client.createOnderwerpobject(
                CreateOnderwerpobjectRequest(
                    klantcontact = NestedUuid(UUID.fromString(KLANTCONTACT_UUID)),
                    onderwerpobjectidentificator =
                        Onderwerpobjectidentificator(
                            objectId = "zaak-1",
                            codeObjecttype = "zaak",
                            codeRegister = "open-zaak",
                            codeSoortObjectId = "uuid",
                        ),
                ),
                properties,
            )

        assertEquals("zaak-1", onderwerpobject.onderwerpobjectidentificator?.objectId)
        assertEquals(UUID.fromString(KLANTCONTACT_UUID), onderwerpobject.klantcontact.uuid)
    }

    // Partijen and partij-identificatoren

    @Test
    fun `getPartij retrieves a single partij`() {
        enqueueBody(PARTIJ_JSON)
        val uuid = UUID.fromString(PARTIJ_UUID)

        val partij = client.getPartij(uuid, properties)

        assertEquals("0000000001", partij.nummer)
        assertEquals("/klantinteracties/api/v1/partijen/$uuid", server.takeRequest().path)
    }

    @Test
    fun `createPartijIdentificator posts the identifying number`() {
        enqueueBody(
            """
            {"uuid":"$PARTIJ_IDENTIFICATOR_UUID","url":"http://ok/partij-identificatoren/$PARTIJ_IDENTIFICATOR_UUID",
             "identificeerdePartij":{"uuid":"$PARTIJ_UUID","url":"http://ok/partijen/$PARTIJ_UUID"},
             "partijIdentificator":{"objectId":"999990676","codeObjecttype":"natuurlijk_persoon",
                                    "codeRegister":"brp","codeSoortObjectId":"bsn"},
             "subIdentificatorVan":null}
            """.trimIndent(),
        )

        val identificator =
            client.createPartijIdentificator(
                CreatePartijIdentificatorRequest(
                    partijIdentificator =
                        Identificator(
                            objectId = "999990676",
                            codeObjecttype = "natuurlijk_persoon",
                            codeRegister = "brp",
                            codeSoortObjectId = "bsn",
                        ),
                    identificeerdePartij = NestedUuid(UUID.fromString(PARTIJ_UUID)),
                ),
                properties,
            )

        assertEquals("999990676", identificator.partijIdentificator.objectId)
        val body = bodyOf(server.takeRequest())
        assertEquals("bsn", body.path("partijIdentificator").path("codeSoortObjectId").asText())
        assertTrue(body.path("subIdentificatorVan").isMissingNode)
    }

    // Rekeningnummers

    @Test
    fun `createRekeningnummer posts the iban`() {
        enqueueBody(REKENINGNUMMER_JSON)

        val rekeningnummer =
            client.createRekeningnummer(
                CreateRekeningnummerRequest(
                    iban = "NL02ABNA0123456789",
                    partij = NestedUuid(UUID.fromString(PARTIJ_UUID)),
                ),
                properties,
            )

        assertEquals("NL02ABNA0123456789", rekeningnummer.iban)
        assertTrue(bodyOf(server.takeRequest()).path("bic").isMissingNode)
    }

    @Test
    fun `patchRekeningnummer sends an empty body when nothing was set`() {
        enqueueBody(REKENINGNUMMER_JSON)

        client.patchRekeningnummer(UUID.fromString(REKENINGNUMMER_UUID), PatchRekeningnummerRequest(), properties)

        assertEquals("{}", server.takeRequest().body.readUtf8())
    }

    // Vertegenwoordigingen

    @Test
    fun `createVertegenwoordiging records who represents whom`() {
        val other = "6a0a1a55-0000-4000-8000-000000000001"
        enqueueBody(
            """
            {"uuid":"$VERTEGENWOORDIGING_UUID","url":"http://ok/vertegenwoordigingen/$VERTEGENWOORDIGING_UUID",
             "vertegenwoordigendePartij":{"uuid":"$PARTIJ_UUID","url":"http://ok/partijen/$PARTIJ_UUID"},
             "vertegenwoordigdePartij":{"uuid":"$other","url":"http://ok/partijen/$other"}}
            """.trimIndent(),
        )

        val vertegenwoordiging =
            client.createVertegenwoordiging(
                CreateVertegenwoordigingRequest(
                    vertegenwoordigendePartij = NestedUuid(UUID.fromString(PARTIJ_UUID)),
                    vertegenwoordigdePartij = NestedUuid(UUID.fromString(other)),
                ),
                properties,
            )

        assertEquals(UUID.fromString(PARTIJ_UUID), vertegenwoordiging.vertegenwoordigendePartij.uuid)
        assertEquals(UUID.fromString(other), vertegenwoordiging.vertegenwoordigdePartij.uuid)
        assertEquals("/klantinteracties/api/v1/vertegenwoordigingen", server.takeRequest().path)
    }

    // Helpers

    private fun enqueueBody(json: String) {
        server.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .setBody(json),
        )
    }

    private fun enqueuePage() = enqueueBody("""{"count":0,"next":null,"previous":null,"results":[]}""")

    private fun bodyOf(request: RecordedRequest): ObjectNode =
        objectMapper.readTree(request.body.readUtf8()) as ObjectNode

    private fun rekeningnummerJson(iban: String): String {
        val uuid = UUID.nameUUIDFromBytes(iban.toByteArray())
        return """{"uuid":"$uuid","url":"http://ok/rekeningnummers/$uuid","iban":"$iban","bic":null}"""
    }

    private companion object {
        const val ACTOR_UUID = "1c1c0001-0000-4000-8000-000000000001"
        const val ACTOR_KLANTCONTACT_UUID = "1c1c0002-0000-4000-8000-000000000002"
        const val KLANTCONTACT_UUID = "1c1c0003-0000-4000-8000-000000000003"
        const val PARTIJ_UUID = "1c1c0004-0000-4000-8000-000000000004"
        const val BIJLAGE_UUID = "1c1c0005-0000-4000-8000-000000000005"
        const val INTERNE_TAAK_UUID = "1c1c0008-0000-4000-8000-000000000008"
        const val ONDERWERPOBJECT_UUID = "1c1c0009-0000-4000-8000-000000000009"
        const val PARTIJ_IDENTIFICATOR_UUID = "1c1c000a-0000-4000-8000-00000000000a"
        const val REKENINGNUMMER_UUID = "1c1c000b-0000-4000-8000-00000000000b"
        const val VERTEGENWOORDIGING_UUID = "1c1c000c-0000-4000-8000-00000000000c"

        val MEDEWERKER_ACTOR_JSON =
            """
            {"uuid":"$ACTOR_UUID","url":"http://ok/actoren/$ACTOR_UUID","naam":"Jamie de Vries",
             "soortActor":"medewerker","indicatieActief":true,"actoridentificator":null,
             "actorIdentificatie":{"functie":"Balie","emailadres":null,"telefoonnummer":"0612345678"}}
            """.trimIndent()

        val BETROKKENE_JSON =
            """
            {"uuid":"1c1c000d-0000-4000-8000-00000000000d","url":"http://ok/betrokkenen/x","wasPartij":null,
             "hadKlantcontact":{"uuid":"$KLANTCONTACT_UUID","url":"http://ok/klantcontacten/$KLANTCONTACT_UUID"},
             "digitaleAdressen":[],"bezoekadres":null,"correspondentieadres":null,"contactnaam":null,
             "volledigeNaam":"","rol":"klant","organisatienaam":"","initiator":true}
            """.trimIndent()

        val INTERNE_TAAK_JSON =
            """
            {"uuid":"$INTERNE_TAAK_UUID","url":"http://ok/internetaken/$INTERNE_TAAK_UUID","nummer":"0000000001",
             "referentienummer":null,"gevraagdeHandeling":"Terugbellen",
             "aanleidinggevendKlantcontact":{"uuid":"$KLANTCONTACT_UUID",
                                             "url":"http://ok/klantcontacten/$KLANTCONTACT_UUID"},
             "toegewezenAanActor":{"uuid":"$ACTOR_UUID","url":"http://ok/actoren/$ACTOR_UUID"},
             "toegewezenAanActoren":[{"uuid":"$ACTOR_UUID","url":"http://ok/actoren/$ACTOR_UUID"}],
             "toelichting":"","status":"te_verwerken","toegewezenOp":"2026-01-01T09:00:00Z","afgehandeldOp":null}
            """.trimIndent()

        val KLANTCONTACT_JSON =
            """
            {"uuid":"$KLANTCONTACT_UUID","url":"http://ok/klantcontacten/$KLANTCONTACT_UUID",
             "gingOverOnderwerpobjecten":[],"hadBetrokkenActoren":[],"omvatteBijlagen":[],"hadBetrokkenen":[],
             "leiddeTotInterneTaken":[],"nummer":"0000000001","referentienummer":null,"kanaal":"telefoon",
             "onderwerp":"Vraag over aanvraag","inhoud":"","reactie":"","indicatieContactGelukt":true,
             "taal":"nld","vertrouwelijk":false,"plaatsgevondenOp":"2026-01-01T09:00:00Z","metadata":{}}
            """.trimIndent()

        val PARTIJ_JSON =
            """
            {"uuid":"$PARTIJ_UUID","url":"http://ok/partijen/$PARTIJ_UUID","nummer":"0000000001",
             "interneNotitie":"","betrokkenen":[],"categorieRelaties":[],"digitaleAdressen":[],
             "voorkeursDigitaalAdres":null,"vertegenwoordigden":[],"rekeningnummers":[],
             "voorkeursRekeningnummer":null,"partijIdentificatoren":[],"soortPartij":"persoon",
             "indicatieGeheimhouding":false,"voorkeurstaal":"nld","indicatieActief":true,"bezoekadres":null,
             "correspondentieadres":null,"partijIdentificatie":null}
            """.trimIndent()

        val REKENINGNUMMER_JSON =
            """
            {"uuid":"$REKENINGNUMMER_UUID","url":"http://ok/rekeningnummers/$REKENINGNUMMER_UUID",
             "partij":{"uuid":"$PARTIJ_UUID","url":"http://ok/partijen/$PARTIJ_UUID"},
             "iban":"NL02ABNA0123456789","bic":"ABNANL2A"}
            """.trimIndent()
    }
}
