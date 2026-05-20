package com.ritense.valtimoplugins.openklant.service

import com.ritense.valtimoplugins.openklant.client.OpenKlantClient
import com.ritense.valtimoplugins.openklant.dto.DigitaalAdresCreationRequest
import com.ritense.valtimoplugins.openklant.dto.DigitaalAdres
import com.ritense.valtimoplugins.openklant.dto.DigitaalAdresPatchRequest
import com.ritense.valtimoplugins.openklant.dto.Klantcontact
import com.ritense.valtimoplugins.openklant.dto.Partij
import com.ritense.valtimoplugins.openklant.dto.SoortDigitaalAdres
import com.ritense.valtimoplugins.openklant.dto.UuidReference
import com.ritense.valtimoplugins.openklant.model.ContactInformation
import com.ritense.valtimoplugins.openklant.model.KlantcontactCreationInformation
import com.ritense.valtimoplugins.openklant.model.KlantcontactOptions
import com.ritense.valtimoplugins.openklant.model.OpenKlantProperties
import com.ritense.valtimoplugins.openklant.model.PartijInformation

class DefaultOpenKlantService(
    private val openKlantClient: OpenKlantClient,
    private val partijFactory: PartijFactory,
    private val klantcontactFactory: KlantcontactFactory,
) : OpenKlantService {
    override fun storeContactInformation(
        contactInformation: ContactInformation,
        properties: OpenKlantProperties,
    ): String {
        val partij = openKlantClient.getPartijByBsn(contactInformation.bsn, properties)
        return if (partij != null) {
            if (!isPreferredAddress(contactInformation.emailadres, partij, properties)) {
                updateExistingPartij(partij, contactInformation, properties)
            }
            partij.uuidReference.toString()
        } else {
            createAndStoreNewPartij(contactInformation, properties)
        }
    }

    override fun getOrCreatePartij(
        partijInformation: PartijInformation,
        properties: OpenKlantProperties,
    ): Partij = openKlantClient.getPartijByBsn(partijInformation.bsn, properties) ?: createNewPartij(
        partijInformation,
        properties
    )

    override fun setDefaultDigitaalAdres(
        digitaalAdresCreationRequest: DigitaalAdresCreationRequest,
        properties: OpenKlantProperties,
    ): DigitaalAdres {
        clearDefaultForCurrentDigitaalAdressen(
            digitaalAdresCreationRequest, properties
        )

        return openKlantClient.createDigitaalAdres(
            request = digitaalAdresCreationRequest,
            properties = properties,
        )
    }

    override fun updateDigitaalAdres(
        digitaalAdresUuid: UuidReference,
        digitaalAdresPatchRequest: DigitaalAdresPatchRequest,
        properties: OpenKlantProperties,
    ): DigitaalAdres =
        openKlantClient.updateDigitaalAdres(
            digitaalAdresUuid = digitaalAdresUuid,
            patchData = digitaalAdresPatchRequest,
            properties = properties
        )


    override fun getAllKlantcontacten(properties: KlantcontactOptions): List<Klantcontact> =
        openKlantClient.getKlantcontacten(properties).results

    override fun postKlantcontact(
        klantcontactCreationInformation: KlantcontactCreationInformation,
        properties: OpenKlantProperties,
    ) {
        val klantContactRequest = klantcontactFactory.createKlantcontactRequest(klantcontactCreationInformation)
        openKlantClient.postKlantcontact(
            request = klantContactRequest,
            properties = properties,
        )
    }

    private fun clearDefaultForCurrentDigitaalAdressen(
        adresInformation: DigitaalAdresCreationRequest,
        properties: OpenKlantProperties,
    ) {
        require(adresInformation.verstrektDoorPartij != null) {
            "Cannot get DefaultAdressen without Partij(UUID)"
        }
        require(adresInformation.referentie != null) {
            "Cannot get DefaultAdressen without 'referentie'"
        }

        return openKlantClient.getDefaultAdressenBySoort(
            partijUuid = adresInformation.verstrektDoorPartij.uuid.toString(),
            soortDigitaalAdres = adresInformation.soortDigitaalAdres,
            referentie = adresInformation.referentie,
            properties = properties,
        ).forEach {
            openKlantClient.updateDigitaalAdres(
                digitaalAdresUuid = it.uuidReference,
                patchData = DigitaalAdresPatchRequest(referentie = ""),
                properties = properties,
            )
        }
    }

    private fun isPreferredAddress(
        emailAddress: String,
        partij: Partij,
        properties: OpenKlantProperties,
    ): Boolean {
        val voorkeursAdresUuid = partij.voorkeursDigitaalAdres?.uuid ?: return false
        val voorkeursAdres = openKlantClient.getDigitaalAdresByUuid(voorkeursAdresUuid.toString(), properties)
        return voorkeursAdres.adres == emailAddress
    }

    private fun createDigitalAddress(
        partij: Partij,
        contactInformation: ContactInformation,
        properties: OpenKlantProperties,
    ): DigitaalAdres = openKlantClient.createDigitaalAdres(
        DigitaalAdresCreationRequest(
            verstrektDoorPartij = partij.uuidReference,
            adres = contactInformation.emailadres,
            soortDigitaalAdres = SoortDigitaalAdres.EMAIL,
            referentie = contactInformation.zaaknummer,
            expand = null
        ),
        properties,
    )

    private fun createNewPartij(
        partijInformation: PartijInformation,
        properties: OpenKlantProperties,
    ): Partij {
        val newPartij = partijFactory.createFromBsn(partijInformation)
        return openKlantClient.createPartij(newPartij, properties)
    }

    private fun updateExistingPartij(
        partij: Partij,
        contactInformation: ContactInformation,
        properties: OpenKlantProperties,
    ) {
        val digitaleAdressen = openKlantClient.getDigitaleAdressenByPartijByUuid(
            partij.getObjectReference().uuid.toString(),
            properties,
        ).toMutableList()

        val digitaleUniekeReferenties = digitaleAdressen.map {
            "${it.verstrektDoorPartij?.uuid},${it.referentie},${it.soortDigitaalAdres}"
        }

        // Maak alleen nieuwe aan wanneer deze uniek is (niet bestaat)
        if ("${partij.uuidReference.uuid},${contactInformation.zaaknummer},${SoortDigitaalAdres.EMAIL}" !in digitaleUniekeReferenties) {
            digitaleAdressen.add(createDigitalAddress(partij, contactInformation, properties))
        }

        updateDigitaleAdressenForPartij(partij, digitaleAdressen.toList(), properties)
    }

    private fun createAndStoreNewPartij(
        contactInformation: ContactInformation,
        properties: OpenKlantProperties,
    ): String {
        val partij = openKlantClient.getPartijByBsn(contactInformation.bsn, properties)
        return if (partij != null) {
            if (!isPreferredAddress(contactInformation.emailadres, partij, properties)) {
                updateExistingPartij(partij, contactInformation, properties)
            }
            partij.uuidReference.toString()
        } else {
            val nieuwePartij = createNewPartij(contactInformation, properties)
            val nieuweDigitaleAdress = createDigitalAddress(nieuwePartij, contactInformation, properties)

            updateDigitaleAdressenForPartij(nieuwePartij, nieuweDigitaleAdress, properties)
            return nieuwePartij.uuidReference.toString()
        }
    }

    private fun updateDigitaleAdressenForPartij(
        partij: Partij,
        digitaleAdressen: List<DigitaalAdres>,
        properties: OpenKlantProperties,
    ) {
        val patchData = mapOf(
            "digitaleAdressen" to digitaleAdressen.map { it.uuidReference },
        )
        openKlantClient.patchPartij(partij.uuidReference.toString(), patchData, properties)
    }

    private fun updateDigitaleAdressenForPartij(
        partij: Partij,
        digitaleAdress: DigitaalAdres,
        properties: OpenKlantProperties,
    ) = updateDigitaleAdressenForPartij(partij, listOf(digitaleAdress), properties)
}
