package com.ritense.valtimoplugins.openklant.service

import com.ritense.valtimoplugins.openklant.client.OpenKlantClient
import com.ritense.valtimoplugins.openklant.dto.CreatePartijRequest
import com.ritense.valtimoplugins.openklant.dto.DigitaalAdresCreationRequest
import com.ritense.valtimoplugins.openklant.dto.DigitaalAdresResponse
import com.ritense.valtimoplugins.openklant.dto.ObjectReference
import com.ritense.valtimoplugins.openklant.dto.Partij
import com.ritense.valtimoplugins.openklant.model.ContactInformation
import com.ritense.valtimoplugins.openklant.model.DigitaalAdres
import com.ritense.valtimoplugins.openklant.model.NestedUuid
import com.ritense.valtimoplugins.openklant.model.OpenKlantProperties
import com.ritense.valtimoplugins.openklant.model.PartijInformationImpl
import com.ritense.valtimoplugins.openklant.model.SoortDigitaalAdres
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
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
        verify(exactly = 0) { client.patchPartij(any<String>(), any<Map<String, Any>>(), any()) }
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
        every { client.patchPartij(any<String>(), any<Map<String, Any>>(), any()) } returns defaultPartij

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
                request = any(),
                properties = testProperties,
            )
        }
        verify {
            client.patchPartij(
                defaultPartij.uuid.toString(),
                match<Map<String, Any>> { partij ->
                    val digitaleAdressen = partij["digitaleAdressen"] as List<UUID>
                    digitaleAdressen.any { it == newDigitaalAdres.uuid }
                },
                testProperties,
            )
        }
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
        every { client.patchPartij(any<String>(), any<Map<String, Any>>(), any()) } returns newPartij

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
                    it.adres == contactInformation.emailadres &&
                        it.soortDigitaalAdres == SoortDigitaalAdres.EMAIL &&
                        it.referentie == contactInformation.zaaknummer
                },
                testProperties,
            )
        }
        verify {
            client.patchPartij(
                newPartij.uuid.toString(),
                match<Map<String, Any>> { partij ->
                    val digitaleAdressen = partij["digitaleAdressen"] as List<UUID>
                    digitaleAdressen.any { it == newDigitaalAdres.uuid }
                },
                testProperties,
            )
        }
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
                        it.verstrektDoorPartij.uuid == digitaalAdres.verstrektDoorPartijUuid &&
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
