package com.ritense.valtimoplugins.openklant.service

import com.ritense.valtimoplugins.openklant.client.OpenKlantClient
import com.ritense.valtimoplugins.openklant.dto.CreatePartijRequest
import com.ritense.valtimoplugins.openklant.dto.DigitaalAdresCreationRequest
import com.ritense.valtimoplugins.openklant.dto.DigitaalAdresResponse
import com.ritense.valtimoplugins.openklant.dto.ObjectReference
import com.ritense.valtimoplugins.openklant.dto.Partij
import com.ritense.valtimoplugins.openklant.model.ContactInformation
import com.ritense.valtimoplugins.openklant.model.DigitaalAdres
import com.ritense.valtimoplugins.openklant.model.DigitaalAdresQuery
import com.ritense.valtimoplugins.openklant.model.NestedUuid
import com.ritense.valtimoplugins.openklant.model.OpenKlantProperties
import com.ritense.valtimoplugins.openklant.model.PartijInformationImpl
import com.ritense.valtimoplugins.openklant.model.SoortDigitaalAdres
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.just
import io.mockk.runs
import io.mockk.slot
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.http.HttpStatus
import org.springframework.web.client.ResourceAccessException
import org.springframework.web.server.ResponseStatusException
import java.net.URI
import java.util.UUID

@ExtendWith(MockKExtension::class)
class OpenKlantServiceTest {
    private val testProperties =
        OpenKlantProperties(
            klantinteractiesUrl = URI("https://example.com"),
            token = "dummy-token",
        )

    @MockK
    lateinit var client: OpenKlantClient

    @MockK
    lateinit var partijFactory: PartijFactory

    @MockK
    lateinit var klantContactFactory: KlantcontactFactory
    lateinit var service: OpenKlantService

    private val defaultDigitaalAdres =
        DigitaalAdresResponse(
            uuid = UUID.fromString("a663831e-74fe-484c-a8bc-f076eed18078"),
            url = "https://example.com",
            verstrektDoorBetrokkene = null,
            verstrektDoorPartij = null,
            adres = "",
            soortDigitaalAdres = SoortDigitaalAdres.OVERIG,
            isStandaardAdres = false,
            omschrijving = null,
            referentie = null,
        )
    private val defaultPartij =
        Partij(
            uuid = UUID.fromString("24c482c7-acec-410f-95e6-72781a9f3064"),
            url = "https://example.com",
            nummer = null,
            interneNotitie = null,
            betrokkenen = emptyList(),
            categorieRelaties = emptyList(),
            digitaleAdressen = null,
            voorkeursDigitaalAdres =
                ObjectReference(
                    uuid = UUID.fromString("4ba49f26-4b22-4183-a269-bd340b10eac8"),
                    url = "https://example.com",
                ),
            vertegenwoordigden = emptyList(),
            rekeningnummers = null,
            voorkeursRekeningnummer = null,
            partijIdentificatoren = null,
            soortPartij = Partij.SoortPartij.PERSOON,
            indicatieGeheimhouding = null,
            voorkeurstaal = null,
            indicatieActief = true,
            bezoekadres = null,
            correspondentieadres = null,
            partijIdentificatie = null,
        )
    private val defaultCreatePartijRequest =
        CreatePartijRequest(
            nummer = "",
            interneNotitie = "",
            digitaleAdressen = null,
            voorkeursDigitaalAdres = null,
            rekeningnummers = null,
            voorkeursRekeningnummer = null,
            partijIdentificatoren = emptyList(),
            soortPartij = Partij.SoortPartij.PERSOON,
            indicatieGeheimhouding = null,
            voorkeurstaal = "nl",
            indicatieActief = true,
            bezoekadres = null,
            correspondentieadres = null,
            partijIdentificatie = null,
        )

    private val contactInformation =
        ContactInformation(
            emailadres = "email@adres.nl",
            zaaknummer = "ZAAK-1234",
            achternaam = "Oe",
            voorvoegselAchternaam = "D",
            voornaam = "John",
            bsn = "123456789",
            voorletters = "",
        )

    private val partijInformation =
        PartijInformationImpl(
            bsn = "123456789",
            voorletters = "J.",
            voornaam = "John",
            voorvoegselAchternaam = "",
            achternaam = "Doe",
        )

    private val ouderUuid = UUID.fromString("0a0a0001-0000-4000-8000-000000000001")
    private val nieuwerUuid = UUID.fromString("0a0a0002-0000-4000-8000-000000000002")

    private fun email(
        uuid: UUID,
        adres: String,
    ) = defaultDigitaalAdres.copy(uuid = uuid, adres = adres, soortDigitaalAdres = SoortDigitaalAdres.EMAIL)

    private val digitaalAdres =
        DigitaalAdres(
            verstrektDoorPartijUuid = UUID.fromString("aaf0d5ec-f2f5-4f41-9d5a-fc04d9cca9df"),
            adres = "test@example.com",
            soortDigitaalAdres = SoortDigitaalAdres.EMAIL,
            referentie = "ref-1",
            verificatieDatum = "2024-01-01",
        )

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        service = DefaultOpenKlantService(client, partijFactory, klantContactFactory)
    }

    @Test
    fun `storeContactInformation should do nothing when supplied email is preferred address`() {
        // ARRANGE:
        every { client.getPartijByBsn(contactInformation.bsn, testProperties) } returns defaultPartij
        every { client.getDigitaalAdres(any(), testProperties) } returns
            defaultDigitaalAdres.copy(
                adres = "email@adres.nl",
                isStandaardAdres = true,
                soortDigitaalAdres = SoortDigitaalAdres.EMAIL,
            )

        // ACT:
        service.storeContactInformation(
            contactInformation = contactInformation,
            properties = testProperties,
        )

        // ASSERT:
        verify { client.getPartijByBsn(contactInformation.bsn, testProperties) }
        verify {
            client.getDigitaalAdres(
                NestedUuid(defaultPartij.voorkeursDigitaalAdres!!.uuid),
                testProperties,
            )
        }
        verify(exactly = 0) { client.createDigitaalAdres(any(), testProperties) }
        verify(exactly = 0) { client.patchPartij(any(), any(), any()) }
        verify(exactly = 0) { client.createPartij(any(), any()) }
    }

    @Test
    fun `storeContactInformation should update existing partij if email is not preferred address`() {
        // ARRANGE:
        every { client.getPartijByBsn(contactInformation.bsn, testProperties) } returns defaultPartij
        every { client.getDigitaalAdres(any(), testProperties) } returns
            defaultDigitaalAdres.copy(
                adres = "email2@adres.nl",
                isStandaardAdres = true,
                soortDigitaalAdres = SoortDigitaalAdres.EMAIL,
            )
        every {
            client.getDigitaleAdressen(
                any(),
                any(),
            )
        } returns listOf()
        val newDigitaalAdres = defaultDigitaalAdres.copy(adres = contactInformation.emailadres)
        every { client.createDigitaalAdres(any(), testProperties) } returns newDigitaalAdres

        // ACT:
        service.storeContactInformation(
            contactInformation = contactInformation,
            properties = testProperties,
        )
        // ASSERT:
        verify { client.getPartijByBsn(contactInformation.bsn, testProperties) }
        verify {
            client.getDigitaalAdres(
                NestedUuid(defaultPartij.voorkeursDigitaalAdres!!.uuid),
                testProperties,
            )
        }
        verify {
            client.getDigitaleAdressen(
                query = any(),
                properties = testProperties,
            )
        }
        verify {
            client.createDigitaalAdres(
                match<DigitaalAdresCreationRequest> {
                    it.verstrektDoorPartij?.uuid == defaultPartij.uuid &&
                        it.adres == contactInformation.emailadres &&
                        it.soortDigitaalAdres == SoortDigitaalAdres.EMAIL &&
                        it.referentie == contactInformation.zaaknummer
                },
                testProperties,
            )
        }
        // The address is linked through verstrektDoorPartij, so the partij itself is never patched.
        verify(exactly = 0) { client.patchPartij(any(), any(), any()) }
        verify(exactly = 0) { client.createPartij(any(), any()) }
    }

    @Test
    fun `storeContactInformation should create a new partij when no partij exists for supplied bsn`() {
        // ARRANGE:
        every { client.getPartijByBsn(contactInformation.bsn, testProperties) } returns null
        val newPartij =
            defaultPartij.copy(uuid = UUID.fromString("d5b806ac-de9b-4024-ae86-c9fab58d53ac"))
        every { partijFactory.createFromBsn(contactInformation) } returns defaultCreatePartijRequest
        every { client.createPartij(defaultCreatePartijRequest, testProperties) } returns newPartij
        val newDigitaalAdres = defaultDigitaalAdres.copy(adres = contactInformation.emailadres)
        every { client.createDigitaalAdres(any(), testProperties) } returns newDigitaalAdres

        // ACT:
        service.storeContactInformation(
            contactInformation = contactInformation,
            properties = testProperties,
        )
        // ASSERT:
        verify { client.getPartijByBsn(contactInformation.bsn, testProperties) }
        verify { partijFactory.createFromBsn(contactInformation) }
        verify { client.createPartij(defaultCreatePartijRequest, testProperties) }
        verify {
            client.createDigitaalAdres(
                match<DigitaalAdresCreationRequest> {
                    it.verstrektDoorPartij?.uuid == newPartij.uuid &&
                        it.adres == contactInformation.emailadres &&
                        it.soortDigitaalAdres == SoortDigitaalAdres.EMAIL &&
                        it.referentie == contactInformation.zaaknummer
                },
                testProperties,
            )
        }
        verify(exactly = 0) { client.patchPartij(any(), any(), any()) }
        verify(exactly = 0) { client.getDigitaalAdres(any(), testProperties) }
    }

    @Test
    fun `getOrCreatePartij should return existing partij when there is a partij for supplied bsn`() {
        // ARRANGE:
        val existingPartij =
            defaultPartij.copy(uuid = UUID.fromString("6648ab61-4c76-466a-bf95-8f25df01aaad"))
        every { client.getPartijByBsn(partijInformation.bsn, testProperties) } returns existingPartij

        val newPartij =
            defaultPartij.copy(uuid = UUID.fromString("222be532-9e4f-4710-adf3-92d2f62097f2"))
        every { client.createPartij(defaultCreatePartijRequest, testProperties) } returns newPartij

        // ACT:
        val resultPartij =
            service.getOrCreatePartij(
                properties = testProperties,
                partijInformation = partijInformation,
            )

        // ASSERT:
        assertEquals("6648ab61-4c76-466a-bf95-8f25df01aaad", resultPartij.uuid.toString())
    }

    @Test
    fun `getOrCreatePartij should create a new partij when no partij exists for supplied bsn`() {
        // ARRANGE:
        every { client.getPartijByBsn(partijInformation.bsn, testProperties) } returns null

        val newPartij =
            defaultPartij.copy(uuid = UUID.fromString("39bc3ca9-3672-4c68-a34a-fe1152c5dfec"))
        every { partijFactory.createFromBsn(partijInformation) } returns defaultCreatePartijRequest
        every { client.createPartij(defaultCreatePartijRequest, testProperties) } returns newPartij

        // ACT:
        val resultPartij =
            service.getOrCreatePartij(
                properties = testProperties,
                partijInformation = partijInformation,
            )

        // ASSERT:
        assertEquals("39bc3ca9-3672-4c68-a34a-fe1152c5dfec", resultPartij.uuid.toString())
    }

    @Test
    fun `getOrCreatePartij returns the partij another process created concurrently`() {
        val concurrent = defaultPartij.copy(uuid = UUID.fromString("5a5a0001-0000-4000-8000-000000000001"))
        every { client.getPartijByBsn(partijInformation.bsn, testProperties) } returnsMany listOf(null, concurrent)
        every { partijFactory.createFromBsn(partijInformation) } returns defaultCreatePartijRequest
        every { client.createPartij(defaultCreatePartijRequest, testProperties) } throws
            ResponseStatusException(HttpStatus.BAD_REQUEST, "PartijIdentificator moet uniek zijn")

        val result = service.getOrCreatePartij(partijInformation, testProperties)

        assertEquals(concurrent.uuid, result.uuid)
    }

    @Test
    fun `getOrCreatePartij rethrows a failed create when the partij still does not exist`() {
        every { client.getPartijByBsn(partijInformation.bsn, testProperties) } returns null
        every { partijFactory.createFromBsn(partijInformation) } returns defaultCreatePartijRequest
        every { client.createPartij(defaultCreatePartijRequest, testProperties) } throws
            ResponseStatusException(HttpStatus.BAD_REQUEST, "invalid")

        assertThrows<ResponseStatusException> { service.getOrCreatePartij(partijInformation, testProperties) }
    }

    @Test
    fun `getOrCreatePartij also falls back when the two creates collide in the database`() {
        val concurrent = defaultPartij.copy(uuid = UUID.fromString("5a5a0002-0000-4000-8000-000000000002"))
        every { client.getPartijByBsn(partijInformation.bsn, testProperties) } returnsMany listOf(null, concurrent)
        every { partijFactory.createFromBsn(partijInformation) } returns defaultCreatePartijRequest
        every { client.createPartij(defaultCreatePartijRequest, testProperties) } throws
            ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "IntegrityError")

        val result = service.getOrCreatePartij(partijInformation, testProperties)

        assertEquals(concurrent.uuid, result.uuid)
    }

    @Test
    fun `getOrCreatePartij does not look again after an error that is not about a concurrent create`() {
        every { client.getPartijByBsn(partijInformation.bsn, testProperties) } returns null
        every { partijFactory.createFromBsn(partijInformation) } returns defaultCreatePartijRequest
        every { client.createPartij(defaultCreatePartijRequest, testProperties) } throws
            ResponseStatusException(HttpStatus.FORBIDDEN, "no access")

        val exception =
            assertThrows<ResponseStatusException> { service.getOrCreatePartij(partijInformation, testProperties) }

        assertEquals(HttpStatus.FORBIDDEN, exception.statusCode)
        verify(exactly = 1) { client.getPartijByBsn(any(), any()) }
    }

    @Test
    fun `getOrCreatePartij keeps the error of the create when the second lookup fails`() {
        val lookupError = ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Open Klant is down")
        var lookups = 0
        every { client.getPartijByBsn(partijInformation.bsn, testProperties) } answers {
            if (lookups++ == 0) null else throw lookupError
        }
        every { partijFactory.createFromBsn(partijInformation) } returns defaultCreatePartijRequest
        every { client.createPartij(defaultCreatePartijRequest, testProperties) } throws
            ResponseStatusException(HttpStatus.BAD_REQUEST, "PartijIdentificator moet uniek zijn")

        val exception =
            assertThrows<ResponseStatusException> { service.getOrCreatePartij(partijInformation, testProperties) }

        assertEquals(HttpStatus.BAD_REQUEST, exception.statusCode)
        assertEquals(listOf(lookupError), exception.suppressed.toList())
    }

    @Test
    fun `getOrCreatePartij refuses a blank bsn instead of matching an arbitrary partij`() {
        assertThrows<IllegalArgumentException> {
            service.getOrCreatePartij(partijInformation.copy(bsn = " "), testProperties)
        }
        verify(exactly = 0) { client.getPartijByBsn(any(), any()) }
    }

    @Test
    fun `getOrCreateOrganisatie returns the existing organisatie for the kvk-nummer`() {
        val existing = defaultPartij.copy(soortPartij = Partij.SoortPartij.ORGANISATIE)
        every { client.getPartijByKvkNummer("12345678", testProperties) } returns existing

        val result = service.getOrCreateOrganisatie("12345678", "Ritense", testProperties)

        assertEquals(existing.uuid, result.uuid)
        verify(exactly = 0) { client.createPartij(any(), any()) }
    }

    @Test
    fun `getOrCreateOrganisatie creates the organisatie when there is none`() {
        val request = defaultCreatePartijRequest.copy(soortPartij = Partij.SoortPartij.ORGANISATIE)
        val created = defaultPartij.copy(soortPartij = Partij.SoortPartij.ORGANISATIE)
        every { client.getPartijByKvkNummer("12345678", testProperties) } returns null
        every { partijFactory.createOrganisatie("12345678", "Ritense") } returns request
        every { client.createPartij(request, testProperties) } returns created

        val result = service.getOrCreateOrganisatie("12345678", "Ritense", testProperties)

        assertEquals(created.uuid, result.uuid)
    }

    @Test
    fun `getOrCreateOrganisatie rejects a kvk-nummer that is not 8 digits`() {
        listOf("1234567", "123456789", "1234567a").forEach { kvk ->
            assertThrows<IllegalArgumentException>(kvk) { service.getOrCreateOrganisatie(kvk, null, testProperties) }
        }
        verify(exactly = 0) { client.getPartijByKvkNummer(any(), any()) }
    }

    @Test
    fun `getOrCreateDigitaalAdres returns the adres the partij already has in another notation`() {
        val query = slot<DigitaalAdresQuery>()
        val partij = UUID.fromString("aaf0d5ec-f2f5-4f41-9d5a-fc04d9cca9df")
        val telefoon = SoortDigitaalAdres.TELEFOONNUMMER
        every { client.getDigitaleAdressen(capture(query), testProperties) } returns
            listOf(
                defaultDigitaalAdres.copy(adres = "0687654321", soortDigitaalAdres = telefoon),
                defaultDigitaalAdres.copy(adres = "06 12 34 56 78", soortDigitaalAdres = telefoon),
            )

        val result =
            service.getOrCreateDigitaalAdres(
                DigitaalAdres(
                    verstrektDoorPartijUuid = partij,
                    adres = "+31 6 1234 5678",
                    soortDigitaalAdres = SoortDigitaalAdres.TELEFOONNUMMER,
                ),
                testProperties,
            )

        assertFalse(result.created)
        assertEquals("06 12 34 56 78", result.value.adres)
        assertEquals(partij.toString(), query.captured.queryParams["verstrektDoorPartij__uuid"])
        assertEquals("telefoonnummer", query.captured.queryParams["soortDigitaalAdres"])
        verify(exactly = 0) { client.createDigitaalAdres(any(), any()) }
    }

    @Test
    fun `getOrCreateDigitaalAdres creates the adres when there is no match`() {
        val created = slot<DigitaalAdresCreationRequest>()
        every { client.getDigitaleAdressen(any(), testProperties) } returns
            listOf(
                defaultDigitaalAdres.copy(adres = "ander@voorbeeld.nl", soortDigitaalAdres = SoortDigitaalAdres.EMAIL),
            )
        every { client.createDigitaalAdres(capture(created), testProperties) } returns
            defaultDigitaalAdres.copy(adres = "Jan@Voorbeeld.nl", soortDigitaalAdres = SoortDigitaalAdres.EMAIL)

        val result = service.getOrCreateDigitaalAdres(digitaalAdres.copy(adres = "Jan@Voorbeeld.nl"), testProperties)

        assertTrue(result.created)
        // The adres is stored as entered; normalising is only for comparing
        assertEquals("Jan@Voorbeeld.nl", created.captured.adres)
    }

    @Test
    fun `getOrCreateDigitaalAdres stores a new telefoonnummer without separators, as Open Klant requires`() {
        val created = slot<DigitaalAdresCreationRequest>()
        every { client.getDigitaleAdressen(any(), testProperties) } returns emptyList()
        every { client.createDigitaalAdres(capture(created), testProperties) } returns defaultDigitaalAdres

        service.getOrCreateDigitaalAdres(
            digitaalAdres.copy(adres = "+31 6 1234 5678", soortDigitaalAdres = SoortDigitaalAdres.TELEFOONNUMMER),
            testProperties,
        )

        assertEquals("0612345678", created.captured.adres)
    }

    @Test
    fun `getOrCreateDigitaalAdres takes the oldest of two equal adressen`() {
        val nieuwer = email(nieuwerUuid, "jan@voorbeeld.nl")
        val ouder = email(ouderUuid, "Jan@Voorbeeld.nl")
        // Open Klant lists newest first
        every { client.getDigitaleAdressen(any(), testProperties) } returns listOf(nieuwer, ouder)

        val result = service.getOrCreateDigitaalAdres(digitaalAdres.copy(adres = "jan@voorbeeld.nl"), testProperties)

        assertFalse(result.created)
        assertEquals(ouderUuid, result.value.uuid)
    }

    @Test
    fun `getOrCreateDigitaalAdres removes its own adres when another process created the same one first`() {
        val ouder = email(ouderUuid, "jan@voorbeeld.nl")
        val eigen = email(nieuwerUuid, "jan@voorbeeld.nl")
        every { client.getDigitaleAdressen(any(), testProperties) } returnsMany
            listOf(emptyList(), listOf(eigen, ouder))
        every { client.createDigitaalAdres(any(), testProperties) } returns eigen
        every { client.deleteDigitaalAdres(nieuwerUuid, testProperties) } just runs

        val result = service.getOrCreateDigitaalAdres(digitaalAdres.copy(adres = "jan@voorbeeld.nl"), testProperties)

        assertFalse(result.created)
        assertEquals(ouderUuid, result.value.uuid)
        verify(exactly = 1) { client.deleteDigitaalAdres(nieuwerUuid, testProperties) }
    }

    @Test
    fun `getOrCreateDigitaalAdres uses the oldest adres when removing its own duplicate fails`() {
        val ouder = email(ouderUuid, "jan@voorbeeld.nl")
        val eigen = email(nieuwerUuid, "jan@voorbeeld.nl")
        every { client.getDigitaleAdressen(any(), testProperties) } returnsMany
            listOf(emptyList(), listOf(eigen, ouder))
        every { client.createDigitaalAdres(any(), testProperties) } returns eigen
        every { client.deleteDigitaalAdres(nieuwerUuid, testProperties) } throws
            ResourceAccessException("Open Klant is not reachable")

        val result = service.getOrCreateDigitaalAdres(digitaalAdres.copy(adres = "jan@voorbeeld.nl"), testProperties)

        assertFalse(result.created)
        assertEquals(ouderUuid, result.value.uuid)
    }

    @Test
    fun `getOrCreateDigitaalAdres looks up and creates under a lock on partij, soort and the normalised adres`() {
        val lock = RecordingLock()
        val lockedService = DefaultOpenKlantService(client, partijFactory, klantContactFactory, lock)
        every { client.getDigitaleAdressen(any(), testProperties) } answers {
            assertTrue(lock.held, "looked up outside the lock")
            emptyList()
        }
        every { client.createDigitaalAdres(any(), testProperties) } answers {
            assertTrue(lock.held, "created outside the lock")
            email(ouderUuid, "jan@voorbeeld.nl")
        }

        lockedService.getOrCreateDigitaalAdres(digitaalAdres.copy(adres = " Jan@Voorbeeld.NL "), testProperties)

        assertEquals(listOf("aaf0d5ec-f2f5-4f41-9d5a-fc04d9cca9df:email:jan@voorbeeld.nl"), lock.keys)
    }

    @Test
    fun `getOrCreateDigitaalAdres keeps its own adres when it is the oldest`() {
        val eigen = email(ouderUuid, "jan@voorbeeld.nl")
        val later = email(nieuwerUuid, "jan@voorbeeld.nl")
        every { client.getDigitaleAdressen(any(), testProperties) } returnsMany
            listOf(emptyList(), listOf(later, eigen))
        every { client.createDigitaalAdres(any(), testProperties) } returns eigen

        val result = service.getOrCreateDigitaalAdres(digitaalAdres.copy(adres = "jan@voorbeeld.nl"), testProperties)

        assertTrue(result.created)
        assertEquals(ouderUuid, result.value.uuid)
        verify(exactly = 0) { client.deleteDigitaalAdres(any(), any()) }
    }

    @Test
    fun `getOrCreateDigitaalAdres looks at the betrokkene when there is no partij`() {
        val query = slot<DigitaalAdresQuery>()
        val betrokkene = UUID.fromString("bbf0d5ec-f2f5-4f41-9d5a-fc04d9cca9df")
        every { client.getDigitaleAdressen(capture(query), testProperties) } returns emptyList()
        every { client.createDigitaalAdres(any(), testProperties) } returns defaultDigitaalAdres

        service.getOrCreateDigitaalAdres(
            DigitaalAdres(
                verstrektDoorBetrokkeneUuid = betrokkene,
                adres = "a@b.nl",
                soortDigitaalAdres = SoortDigitaalAdres.EMAIL,
            ),
            testProperties,
        )

        assertEquals(betrokkene.toString(), query.captured.queryParams["verstrektDoorBetrokkene__uuid"])
        assertFalse(query.captured.queryParams.containsKey("verstrektDoorPartij__uuid"))
    }

    @Test
    fun `setDefaultDigitaalAdres clears existing defaults and creates new one`() {
        // ARRANGE
        val existingAdres =
            DigitaalAdresResponse(
                uuid = UUID.fromString("0853ba19-7c5e-405b-af4a-60689c0b4e85"),
                url = "url1",
                verstrektDoorBetrokkene = null,
                verstrektDoorPartij = null,
                adres = "old@test.com",
                soortDigitaalAdres = SoortDigitaalAdres.EMAIL,
                isStandaardAdres = true,
                omschrijving = null,
                referentie = "old-ref",
            )

        every {
            client.getDigitaleAdressen(any(), any())
        } returns listOf(existingAdres)

        val createdResult =
            existingAdres.copy(
                uuid = UUID.fromString("0853ba19-7c5e-405b-af4a-60689c0b4e85"),
            )
        every {
            client.createDigitaalAdres(any(), any())
        } returns createdResult

        val adjustedAdres = existingAdres.copy(referentie = "")
        every {
            client.updateDigitaalAdres(any(), any(), any())
        } returns adjustedAdres

        // ACT
        val result =
            service.setDefaultDigitaalAdres(
                request = digitaalAdres,
                properties = testProperties,
            )

        // ASSERT
        verify {
            client.createDigitaalAdres(
                request =
                    match {
                        it.verstrektDoorPartij?.uuid == digitaalAdres.verstrektDoorPartijUuid &&
                            it.adres == digitaalAdres.adres &&
                            it.soortDigitaalAdres == digitaalAdres.soortDigitaalAdres &&
                            it.referentie == digitaalAdres.referentie
                    },
                properties = testProperties,
            )
        }

        assertEquals("0853ba19-7c5e-405b-af4a-60689c0b4e85", result.uuid.toString())
    }
}

private class RecordingLock : DigitaalAdresLock {
    val keys = mutableListOf<String>()
    var held = false

    override fun <T> withLock(
        key: String,
        block: () -> T,
    ): T {
        keys += key
        held = true
        try {
            return block()
        } finally {
            held = false
        }
    }
}
