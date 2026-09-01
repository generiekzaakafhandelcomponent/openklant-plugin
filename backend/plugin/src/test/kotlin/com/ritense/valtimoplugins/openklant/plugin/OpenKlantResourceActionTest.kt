package com.ritense.valtimoplugins.openklant.plugin

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.ritense.plugin.annotation.PluginAction
import com.ritense.plugin.annotation.PluginActionProperty
import com.ritense.valtimoplugins.openklant.client.OpenKlantClient
import com.ritense.valtimoplugins.openklant.dto.Actor
import com.ritense.valtimoplugins.openklant.dto.Betrokkene
import com.ritense.valtimoplugins.openklant.dto.CreateActorRequest
import com.ritense.valtimoplugins.openklant.dto.CreateBetrokkeneRequest
import com.ritense.valtimoplugins.openklant.dto.CreateInterneTaakRequest
import com.ritense.valtimoplugins.openklant.dto.CreateOnderwerpobjectRequest
import com.ritense.valtimoplugins.openklant.dto.CreatePartijRequest
import com.ritense.valtimoplugins.openklant.dto.InterneTaak
import com.ritense.valtimoplugins.openklant.dto.InterneTaakStatus
import com.ritense.valtimoplugins.openklant.dto.Klantcontact
import com.ritense.valtimoplugins.openklant.dto.KlantcontactCreationRequest
import com.ritense.valtimoplugins.openklant.dto.MaakKlantcontactResponse
import com.ritense.valtimoplugins.openklant.dto.Onderwerpobject
import com.ritense.valtimoplugins.openklant.dto.Partij
import com.ritense.valtimoplugins.openklant.dto.PatchPartijRequest
import com.ritense.valtimoplugins.openklant.dto.PatchRekeningnummerRequest
import com.ritense.valtimoplugins.openklant.dto.Rekeningnummer
import com.ritense.valtimoplugins.openklant.dto.UuidAndUrlReference
import com.ritense.valtimoplugins.openklant.model.DigitaalAdres
import com.ritense.valtimoplugins.openklant.model.OpenKlantProperties
import com.ritense.valtimoplugins.openklant.model.OpenKlantQuery
import com.ritense.valtimoplugins.openklant.model.SoortDigitaalAdres
import com.ritense.valtimoplugins.openklant.service.OpenKlantService
import com.ritense.valtimoplugins.openklant.util.ReflectionUtil
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.operaton.bpm.engine.delegate.DelegateExecution
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method
import java.lang.reflect.Parameter
import java.net.URI
import java.util.UUID

/** Invokes the resource plugin actions the way PluginService does, resolving action properties by name. */
internal class OpenKlantResourceActionTest {
    private val client: OpenKlantClient = mockk(relaxed = true)
    private val openKlantService: OpenKlantService = mockk(relaxed = true)
    private val objectMapper = ObjectMapper().registerKotlinModule()
    private val execution: DelegateExecution = mockk(relaxed = true)
    private val storedVariables = mutableMapOf<String, Any?>()

    private val plugin =
        OpenKlantPlugin(
            openKlantPluginService = openKlantService,
            openKlantClient = client,
            reflectionUtil = ReflectionUtil(),
            objectMapper = objectMapper,
        ).apply {
            klantinteractiesUrl = URI("https://open-klant.example.com/klantinteracties/api/v1/")
            token = "test-token"
        }

    init {
        every { execution.setVariable(any(), any()) } answers
            {
                storedVariables[firstArg()] = secondArg()
                Unit
            }
    }

    // Plugin metadata

    @Test
    fun `every plugin action has a unique key`() {
        val keys = actionKeys()

        assertEquals(
            keys.size,
            keys.toSet().size,
            "duplicate plugin action keys: ${keys.groupingBy { it }.eachCount().filterValues { it > 1 }.keys}",
        )
    }

    @Test
    fun `exposes an action for every klantinteracties resource`() {
        val keys = actionKeys().toSet()

        listOf(
            "actor",
            "actorklantcontact",
            "betrokkene",
            "bijlage",
            "internetaak",
            "klantcontact",
            "onderwerpobject",
            "partij",
            "partij-identificator",
            "rekeningnummer",
            "vertegenwoordiging",
        ).forEach { resource ->
            assertTrue("create-$resource" in keys, "missing create action for $resource")
            assertTrue("get-$resource" in keys, "missing get action for $resource")
            assertTrue("delete-$resource" in keys, "missing delete action for $resource")
        }
        assertTrue("get-digitaal-adres" in keys)
        assertTrue("delete-digitaal-adres" in keys)
    }

    @Test
    fun `does not expose the experimental categorie endpoints`() {
        val keys = actionKeys()

        assertTrue(
            keys.none { it.contains("categorie") },
            "categorie and categorie-relatie are marked EXPERIMENTEEL by Open Klant and must not be shipped",
        )
    }

    // List actions

    @Test
    fun `list action turns the configured key-value pairs into query parameters`() {
        val query = slot<OpenKlantQuery>()
        every { client.getRekeningnummers(capture(query), any()) } returns emptyList()

        invoke(
            "get-rekeningnummers",
            mapOf(
                "resultPvName" to "rekeningnummers",
                "queryParams" to
                    listOf(
                        mapOf("key" to "pageSize", "value" to "50"),
                        mapOf("key" to "page", "value" to "2"),
                    ),
            ),
        )

        assertEquals(mapOf("pageSize" to "50", "page" to "2"), query.captured.queryParams)
    }

    @Test
    fun `list action stores the results as JSON in the configured process variable`() {
        every { client.getRekeningnummers(any(), any()) } returns listOf(rekeningnummer())

        invoke("get-rekeningnummers", mapOf("resultPvName" to "rekeningnummers", "queryParams" to emptyList<Any>()))

        val stored = storedVariables["rekeningnummers"] as JsonNode
        assertEquals("NL02ABNA0123456789", stored.first().path("iban").asText())
    }

    @Test
    fun `list action passes the plugin configuration as properties`() {
        val properties = slot<OpenKlantProperties>()
        every { client.getActoren(any(), capture(properties)) } returns emptyList()

        invoke("get-actoren", mapOf("resultPvName" to "actoren", "queryParams" to emptyList<Any>()))

        assertEquals(
            URI("https://open-klant.example.com/klantinteracties/api/v1/"),
            properties.captured.klantinteractiesUrl,
        )
        assertEquals("test-token", properties.captured.token)
    }

    // Create actions

    @Test
    fun `create-actor maps the configured fields onto the request`() {
        val request = slot<CreateActorRequest>()
        every { client.createActor(capture(request), any()) } returns actor()

        invoke(
            "create-actor",
            mapOf(
                "naam" to " Jamie de Vries ",
                "soortActor" to "MEDEWERKER",
                "indicatieActief" to "true",
                "functie" to "Balie",
                "telefoonnummer" to "0612345678",
                "objectId" to "abc",
                "codeObjecttype" to "medewerker",
                "resultPvName" to "actor",
            ),
        )

        assertEquals("Jamie de Vries", request.captured.naam)
        assertEquals(Actor.SoortActor.MEDEWERKER, request.captured.soortActor)
        assertEquals(true, request.captured.indicatieActief)
        assertEquals("Balie", request.captured.actorIdentificatie?.functie)
        assertEquals("0612345678", request.captured.actorIdentificatie?.telefoonnummer)
        assertNull(request.captured.actorIdentificatie?.faxnummer)
        assertEquals("abc", request.captured.actoridentificator?.objectId)
        assertNull(request.captured.actoridentificator?.codeRegister)
    }

    @Test
    fun `create-actor leaves out the nested objects when none of their fields are configured`() {
        val request = slot<CreateActorRequest>()
        every { client.createActor(capture(request), any()) } returns actor()

        invoke(
            "create-actor",
            mapOf("naam" to "Robot", "soortActor" to "geautomatiseerde_actor", "resultPvName" to "actor"),
        )

        assertNull(request.captured.actorIdentificatie)
        assertNull(request.captured.actoridentificator)
        assertNull(request.captured.indicatieActief)
    }

    @Test
    fun `create-betrokkene defaults initiator to false and builds the contactnaam`() {
        val request = slot<CreateBetrokkeneRequest>()
        every { client.createBetrokkene(capture(request), any()) } returns betrokkene()

        invoke(
            "create-betrokkene",
            mapOf(
                "klantcontactUuid" to UUID_A,
                "rol" to "klant",
                "voornaam" to "Pietje",
                "achternaam" to "Puk",
                "resultPvName" to "betrokkene",
            ),
        )

        assertEquals(UUID.fromString(UUID_A), request.captured.hadKlantcontact.uuid)
        assertEquals(Betrokkene.Rol.KLANT, request.captured.rol)
        assertEquals(false, request.captured.initiator)
        assertEquals("Pietje", request.captured.contactnaam?.voornaam)
        assertEquals("Puk", request.captured.contactnaam?.achternaam)
        assertNull(request.captured.contactnaam?.voorletters)
        assertNull(request.captured.wasPartij)
    }

    @Test
    fun `create-betrokkene reads an address object from a process variable`() {
        val request = slot<CreateBetrokkeneRequest>()
        every { client.createBetrokkene(capture(request), any()) } returns betrokkene()

        invoke(
            "create-betrokkene",
            mapOf(
                "klantcontactUuid" to UUID_A,
                "rol" to "klant",
                "resultPvName" to "betrokkene",
                "bezoekadres" to
                    mapOf(
                        "straatnaam" to "Dorpsstraat",
                        "huisnummer" to 1,
                        "postcode" to "1234 AB",
                        "stad" to "Utrecht",
                    ),
            ),
        )

        assertEquals("Dorpsstraat", request.captured.bezoekadres?.straatnaam)
        assertEquals(1, request.captured.bezoekadres?.huisnummer)
        assertEquals("Utrecht", request.captured.bezoekadres?.stad)
        assertNull(request.captured.correspondentieadres)
    }

    @Test
    fun `create-internetaak splits the comma separated actor uuids`() {
        val request = slot<CreateInterneTaakRequest>()
        every { client.createInterneTaak(capture(request), any()) } returns interneTaak()

        invoke(
            "create-internetaak",
            mapOf(
                "gevraagdeHandeling" to "Terugbellen",
                "klantcontactUuid" to UUID_A,
                "status" to "te_verwerken",
                "toegewezenAanActoren" to "$UUID_B, $UUID_C",
                "resultPvName" to "taak",
            ),
        )

        assertEquals(InterneTaakStatus.TE_VERWERKEN, request.captured.status)
        assertEquals(
            listOf(UUID.fromString(UUID_B), UUID.fromString(UUID_C)),
            request.captured.toegewezenAanActoren?.map { it.uuid },
        )
    }

    @Test
    fun `create-onderwerpobject requires all four identificator fields once one is configured`() {
        val exception =
            assertThrows<IllegalArgumentException> {
                invoke(
                    "create-onderwerpobject",
                    mapOf(
                        "klantcontactUuid" to UUID_A,
                        "objectId" to "zaak-1",
                        "resultPvName" to "onderwerpobject",
                    ),
                )
            }

        assertEquals("Action property 'codeObjecttype' is required", exception.message)
    }

    @Test
    fun `create-onderwerpobject omits the identificator when none of its fields are configured`() {
        val request = slot<CreateOnderwerpobjectRequest>()
        every { client.createOnderwerpobject(capture(request), any()) } returns onderwerpobject()

        invoke(
            "create-onderwerpobject",
            mapOf("klantcontactUuid" to UUID_A, "resultPvName" to "onderwerpobject"),
        )

        assertNull(request.captured.onderwerpobjectidentificator)
        assertEquals(UUID.fromString(UUID_A), request.captured.klantcontact?.uuid)
    }

    @Test
    fun `create-partij requires a partij-identificator so the partij stays findable`() {
        val exception =
            assertThrows<IllegalArgumentException> {
                invoke("create-partij", mapOf("soortPartij" to "persoon", "resultPvName" to "partij"))
            }

        assertEquals("Action property 'objectId' is required", exception.message)
    }

    @Test
    fun `create-partij sends the configured partij-identificator`() {
        val request = slot<CreatePartijRequest>()
        every { client.createPartij(capture(request), any()) } returns partij()

        invoke(
            "create-partij",
            mapOf(
                "soortPartij" to "persoon",
                "objectId" to "999990676",
                "codeObjecttype" to "natuurlijk_persoon",
                "codeRegister" to "brp",
                "codeSoortObjectId" to "bsn",
                "achternaam" to "Puk",
                "resultPvName" to "partij",
            ),
        )

        val identificator =
            request.captured.partijIdentificatoren
                .single()
                .getValue("partijIdentificator")
        assertEquals("999990676", identificator.objectId)
        assertEquals("bsn", identificator.codeSoortObjectId)
        assertEquals(Partij.SoortPartij.PERSOON, request.captured.soortPartij)
    }

    @Test
    fun `create-klantcontact requires a betrokkene`() {
        val exception =
            assertThrows<IllegalArgumentException> {
                invoke(
                    "create-klantcontact",
                    mapOf(
                        "kanaal" to "telefoon",
                        "onderwerp" to "Vraag",
                        "taal" to "nld",
                        "objectId" to "zaak-1",
                        "codeObjecttype" to "zaak",
                        "codeRegister" to "open-zaak",
                        "codeSoortObjectId" to "uuid",
                        "resultPvName" to "klantcontact",
                    ),
                )
            }

        assertEquals("Action property 'rol' is required", exception.message)
    }

    @Test
    fun `create-klantcontact requires an onderwerpobject`() {
        val exception =
            assertThrows<IllegalArgumentException> {
                invoke(
                    "create-klantcontact",
                    mapOf(
                        "kanaal" to "telefoon",
                        "onderwerp" to "Vraag",
                        "taal" to "nld",
                        "rol" to "klant",
                        "resultPvName" to "klantcontact",
                    ),
                )
            }

        assertEquals("Action property 'objectId' is required", exception.message)
    }

    @Test
    fun `create-klantcontact creates the klantcontact, betrokkene and onderwerpobject in one call`() {
        val request = slot<KlantcontactCreationRequest>()
        every { client.maakKlantcontact(capture(request), any()) } returns
            MaakKlantcontactResponse(klantcontact = klantcontact())

        invoke(
            "create-klantcontact",
            mapOf(
                "kanaal" to "telefoon",
                "onderwerp" to "Vraag over aanvraag",
                "taal" to "nld",
                "vertrouwelijk" to "false",
                "rol" to "klant",
                "initiator" to "true",
                "partijUuid" to UUID_B,
                "achternaam" to "Puk",
                "objectId" to "095be615-a8ad-4c33-8e9c-c7612fbf6c9f",
                "codeObjecttype" to "zaak",
                "codeRegister" to "open-zaak",
                "codeSoortObjectId" to "uuid",
                "resultPvName" to "klantcontact",
            ),
        )

        assertEquals("telefoon", request.captured.klantcontact.kanaal)
        assertEquals(Betrokkene.Rol.KLANT, request.captured.betrokkene?.rol)
        assertEquals(true, request.captured.betrokkene?.initiator)
        assertEquals(
            UUID.fromString(UUID_B),
            request.captured.betrokkene
                ?.wasPartij
                ?.uuid,
        )
        assertEquals(
            "Puk",
            request.captured.betrokkene
                ?.contactnaam
                ?.achternaam,
        )
        assertEquals(
            "zaak",
            request.captured.onderwerpobject
                ?.onderwerpobjectidentificator
                ?.codeObjecttype,
        )
    }

    @Test
    fun `create action stores only the uuid of the created object`() {
        every { client.createActor(any(), any()) } returns actor()

        invoke(
            "create-actor",
            mapOf("naam" to "Robot", "soortActor" to "geautomatiseerde_actor", "resultPvName" to "actor"),
        )

        assertEquals(UUID_A, storedVariables["actor"])
    }

    @Test
    fun `create action stores nothing when the result process variable is left blank`() {
        every { client.createActor(any(), any()) } returns actor()

        invoke(
            "create-actor",
            mapOf("naam" to "Robot", "soortActor" to "geautomatiseerde_actor", "resultPvName" to "  "),
        )

        assertTrue(storedVariables.isEmpty())
    }

    @Test
    fun `create-klantcontact stores a reference to each of the three created objects`() {
        every { client.maakKlantcontact(any(), any()) } returns
            MaakKlantcontactResponse(
                klantcontact = klantcontact(),
                betrokkene = betrokkene(),
                onderwerpobject = onderwerpobject(),
            )

        invoke(
            "create-klantcontact",
            mapOf(
                "kanaal" to "telefoon",
                "onderwerp" to "Vraag",
                "taal" to "nld",
                "rol" to "klant",
                "objectId" to "zaak-1",
                "codeObjecttype" to "zaak",
                "codeRegister" to "open-zaak",
                "codeSoortObjectId" to "uuid",
                "resultPvName" to "klantcontact",
                "betrokkeneResultPvName" to "betrokkene",
                "onderwerpobjectResultPvName" to "onderwerpobject",
            ),
        )

        assertEquals(UUID_A, storedVariables["klantcontact"])
        assertEquals(UUID_B, storedVariables["betrokkene"])
        assertEquals(UUID_B, storedVariables["onderwerpobject"])
    }

    // Update actions

    @Test
    fun `update action stores only the uuid of the updated object`() {
        every { client.patchRekeningnummer(any(), any<PatchRekeningnummerRequest>(), any()) } returns rekeningnummer()

        invoke("update-rekeningnummer", mapOf("uuid" to UUID_A, "iban" to "NL02ABNA0123456789", "resultPvName" to "rn"))

        assertEquals(UUID_B, storedVariables["rn"])
    }

    @Test
    fun `update action only forwards the fields that were configured`() {
        val request = slot<PatchRekeningnummerRequest>()
        every { client.patchRekeningnummer(any(), capture(request), any()) } returns rekeningnummer()

        invoke(
            "update-rekeningnummer",
            mapOf("uuid" to UUID_A, "iban" to "NL02ABNA0123456789", "bic" to "", "resultPvName" to "rekeningnummer"),
        )

        assertEquals("NL02ABNA0123456789", request.captured.iban)
        assertNull(request.captured.bic)
        assertNull(request.captured.partij)
    }

    @Test
    fun `update-partij forwards the uuid from the action property`() {
        val uuid = slot<UUID>()
        every { client.patchPartij(capture(uuid), any<PatchPartijRequest>(), any()) } returns partij()

        invoke("update-partij", mapOf("uuid" to UUID_A, "nummer" to "0000000001", "resultPvName" to "partij"))

        assertEquals(UUID.fromString(UUID_A), uuid.captured)
    }

    // Delete actions

    @Test
    fun `delete action forwards the uuid and stores nothing`() {
        invoke("delete-rekeningnummer", mapOf("uuid" to UUID_A))

        verify(exactly = 1) { client.deleteRekeningnummer(UUID.fromString(UUID_A), any()) }
        assertTrue(storedVariables.isEmpty())
    }

    // Validation

    @Test
    fun `a missing uuid fails with a message naming the action property`() {
        val exception =
            assertThrows<IllegalArgumentException> { invoke("delete-rekeningnummer", mapOf("uuid" to "  ")) }

        assertEquals("Action property 'uuid' is required and must contain a UUID", exception.message)
    }

    @Test
    fun `a malformed uuid fails fast`() {
        assertThrows<IllegalArgumentException> { invoke("delete-rekeningnummer", mapOf("uuid" to "not-a-uuid")) }
    }

    @Test
    fun `an unknown enum value fails with a message listing the supported values`() {
        val exception =
            assertThrows<IllegalArgumentException> {
                invoke(
                    "create-internetaak",
                    mapOf(
                        "gevraagdeHandeling" to "Terugbellen",
                        "klantcontactUuid" to UUID_A,
                        "status" to "afgerond",
                        "resultPvName" to "taak",
                    ),
                )
            }

        assertEquals(
            "Unknown interne taak status 'afgerond'. Supported values: te_verwerken, verwerkt",
            exception.message,
        )
    }

    // Backwards compatibility of the pre-existing actions
    //
    // These actions shipped before the resource actions were added and processes are already reading
    // their output, so the shape they publish must not change.

    @Test
    fun `create-digitaal-adres keeps publishing the whole object, not just its uuid`() {
        every { openKlantService.createDigitaalAdres(any(), any()) } returns digitaalAdres()

        invoke(
            "create-digitaal-adres",
            mapOf(
                "resultPvName" to "digitaalAdres",
                "verstrektDoorPartij" to UUID_B,
                "adres" to "jamie@example.com",
                "soortDigitaalAdres" to "email",
                "omschrijving" to "",
            ),
        )

        val stored = storedVariables["digitaalAdres"] as JsonNode
        assertEquals("jamie@example.com", stored.path("adres").asText())
        assertEquals(UUID_A, stored.path("uuid").asText())
    }

    @Test
    fun `update-digitaal-adres keeps publishing the whole object, not just its uuid`() {
        every { openKlantService.updateDigitaalAdres(any(), any(), any()) } returns digitaalAdres()

        invoke(
            "update-digitaal-adres",
            mapOf("resultPvName" to "digitaalAdres", "digitaalAdresUuid" to UUID_A, "adres" to "jamie@example.com"),
        )

        val stored = storedVariables["digitaalAdres"] as JsonNode
        assertEquals("jamie@example.com", stored.path("adres").asText())
    }

    @Test
    fun `set-default-digitaal-adres keeps publishing only the uuid`() {
        every { openKlantService.setDefaultDigitaalAdres(any(), any()) } returns digitaalAdres()

        invoke(
            "set-default-digitaal-adres",
            mapOf(
                "resultPvName" to "digitaalAdresUuid",
                "partijUuid" to UUID_B,
                "adres" to "jamie@example.com",
                "soortDigitaalAdres" to "email",
                "verificatieDatum" to "2026-01-01",
            ),
        )

        assertEquals(UUID_A, storedVariables["digitaalAdresUuid"])
    }

    @Test
    fun `contact history actions keep publishing reflected maps with string-encoded booleans`() {
        every { openKlantService.getAllKlantcontacten(any(), any()) } returns listOf(klantcontact())

        listOf(
            "get-contact-moments-by-case-uuid" to mapOf("caseUuid" to UUID_B),
            "get-contact-moments-by-bsn" to mapOf("bsn" to "999990123"),
            "get-contact-moments-by-partij-uuid" to mapOf("partijUuid" to UUID_B),
        ).forEach { (key, properties) ->
            storedVariables.clear()

            invoke(key, properties + mapOf("resultPvName" to "contactgeschiedenis"))

            @Suppress("UNCHECKED_CAST")
            val stored = storedVariables["contactgeschiedenis"] as List<Map<String, Any?>>
            assertEquals("false", stored.single()["vertrouwelijk"], "$key must keep encoding booleans as strings")
            assertEquals("0000000001", stored.single()["nummer"])
        }
    }

    @Test
    fun `the klantcontact expand property keeps the name the klant value resolver reflects over`() {
        // The resolver reflects over Kotlin property names, so this must stay 'expand' rather than '_expand'.
        assertTrue(
            Klantcontact::class.java.declaredFields.any { it.name == "expand" },
            "renaming this field silently changes the key exposed by the 'klant:' value resolver",
        )
    }

    // Helpers

    private fun digitaalAdres() =
        DigitaalAdres(
            uuid = UUID.fromString(UUID_A),
            url = URI("http://ok/digitaleadressen/$UUID_A"),
            adres = "jamie@example.com",
            soortDigitaalAdres = SoortDigitaalAdres.EMAIL,
        )

    private fun invoke(
        key: String,
        actionProperties: Map<String, Any?>,
    ) {
        val method = actionMethod(key)
        val arguments =
            method.parameters
                .map { parameter ->
                    if (parameter.isAnnotationPresent(PluginActionProperty::class.java)) {
                        coerce(actionProperties[parameter.name], parameter)
                    } else {
                        execution
                    }
                }.toTypedArray()

        try {
            method.invoke(plugin, *arguments)
        } catch (e: InvocationTargetException) {
            throw e.targetException
        }
    }

    /** Mirrors how PluginService binds process-link JSON onto action parameters with Jackson. */
    private fun coerce(
        value: Any?,
        parameter: Parameter,
    ): Any? {
        if (value == null) {
            return null
        }
        val type = objectMapper.constructType(parameter.parameterizedType)
        return if (type.rawClass.isInstance(value) && value !is Collection<*> && value !is Map<*, *>) {
            value
        } else {
            objectMapper.convertValue(value, type)
        }
    }

    private fun actionMethod(key: String): Method =
        OpenKlantPlugin::class.java.methods.single { method ->
            method.getAnnotation(PluginAction::class.java)?.key == key
        }

    private fun actionKeys(): List<String> =
        OpenKlantPlugin::class.java.methods.mapNotNull { it.getAnnotation(PluginAction::class.java)?.key }

    private fun actor() =
        Actor(
            uuid = UUID.fromString(UUID_A),
            url = "http://ok/actoren/$UUID_A",
            naam = "Jamie de Vries",
            soortActor = Actor.SoortActor.MEDEWERKER,
        )

    private fun reference(uuid: String) = UuidAndUrlReference(UUID.fromString(uuid), "http://ok/$uuid")

    private fun betrokkene() =
        Betrokkene(
            uuid = UUID.fromString(UUID_B),
            url = "http://ok/betrokkenen/$UUID_B",
            hadKlantcontact = reference(UUID_A),
            digitaleAdressen = emptyList(),
            volledigeNaam = "Pietje Puk",
            rol = Betrokkene.Rol.KLANT,
            initiator = false,
        )

    private fun interneTaak() =
        InterneTaak(
            uuid = UUID.fromString(UUID_B),
            url = "http://ok/internetaken/$UUID_B",
            gevraagdeHandeling = "Terugbellen",
            aanleidinggevendKlantcontact = reference(UUID_A),
            status = InterneTaakStatus.TE_VERWERKEN,
        )

    private fun onderwerpobject() =
        Onderwerpobject(
            uuid = UUID.fromString(UUID_B),
            url = "http://ok/onderwerpobjecten/$UUID_B",
            klantcontact = reference(UUID_A),
        )

    private fun klantcontact() =
        Klantcontact(
            uuid = UUID.fromString(UUID_A),
            url = "http://ok/klantcontacten/$UUID_A",
            gingOverOnderwerpobjecten = emptyList(),
            hadBetrokkenActoren = emptyList(),
            omvatteBijlagen = emptyList(),
            hadBetrokkenen = emptyList(),
            leiddeTotInterneTaken = emptyList(),
            nummer = "0000000001",
            referentienummer = null,
            kanaal = "telefoon",
            onderwerp = "Vraag over aanvraag",
            inhoud = null,
            reactie = null,
            indicatieContactGelukt = null,
            taal = "nld",
            vertrouwelijk = false,
            plaatsgevondenOp = null,
            metadata = emptyMap(),
            expand = null,
        )

    private fun rekeningnummer() =
        Rekeningnummer(
            uuid = UUID.fromString(UUID_B),
            url = "http://ok/rekeningnummers/$UUID_B",
            iban = "NL02ABNA0123456789",
        )

    private fun partij() =
        Partij(
            uuid = UUID.fromString(UUID_B),
            url = "http://ok/partijen/$UUID_B",
            nummer = "0000000001",
            interneNotitie = "",
            betrokkenen = emptyList(),
            categorieRelaties = emptyList(),
            digitaleAdressen = emptyList(),
            voorkeursDigitaalAdres = null,
            vertegenwoordigden = emptyList(),
            rekeningnummers = emptyList(),
            voorkeursRekeningnummer = null,
            partijIdentificatoren = emptyList(),
            soortPartij = Partij.SoortPartij.PERSOON,
            indicatieGeheimhouding = false,
            voorkeurstaal = "nld",
            indicatieActief = true,
            bezoekadres = null,
            correspondentieadres = null,
            partijIdentificatie = null,
        )

    private companion object {
        const val UUID_A = "3e3e0001-0000-4000-8000-000000000001"
        const val UUID_B = "3e3e0002-0000-4000-8000-000000000002"
        const val UUID_C = "3e3e0003-0000-4000-8000-000000000003"
    }
}
