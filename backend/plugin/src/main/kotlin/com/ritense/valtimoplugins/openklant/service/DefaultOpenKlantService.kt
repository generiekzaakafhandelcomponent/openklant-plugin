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
import com.ritense.valtimoplugins.openklant.model.DigitaalAdresQuery
import com.ritense.valtimoplugins.openklant.model.DigitaalAdresQueryParamNames
import com.ritense.valtimoplugins.openklant.model.KlantcontactCreationInformation
import com.ritense.valtimoplugins.openklant.model.KlantcontactQuery
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
            if (!isPreferredAddress(
                    emailAddress = contactInformation.emailadres,
                    partij = partij,
                    properties = properties
                )
            ) {
                updateExistingPartij(
                    partij = partij,
                    contactInformation = contactInformation,
                    properties = properties
                )
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

    override fun getAllDigitaleAdressen(
        query: DigitaalAdresQuery,
        properties: OpenKlantProperties
    ): List<DigitaalAdres> =
        openKlantClient.getDigitaleAdressen(
            query = query,
            properties = properties
        )

    override fun setDefaultDigitaalAdres(
        request: DigitaalAdresCreationRequest,
        properties: OpenKlantProperties,
    ): DigitaalAdres {
        clearDefaultForCurrentDigitaalAdressen(
            request, properties
        )

        return openKlantClient.createDigitaalAdres(
            request = request,
            properties = properties,
        )
    }

    override fun updateDigitaalAdres(
        digitaalAdresUuid: UuidReference,
        request: DigitaalAdresPatchRequest,
        properties: OpenKlantProperties,
    ): DigitaalAdres =
        openKlantClient.updateDigitaalAdres(
            digitaalAdresUuid = digitaalAdresUuid,
            patchData = request,
            properties = properties
        )


    override fun getAllKlantcontacten(query: KlantcontactQuery, properties: OpenKlantProperties): List<Klantcontact> =
        openKlantClient.getKlantcontacten(query, properties).results

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

        val query = DigitaalAdresQuery()

        query.add(
            paramName = DigitaalAdresQueryParamNames.HADBETROKKENE__WASPARTIJ__UUID.value,
            value = adresInformation.verstrektDoorPartij.toString()
        )
        query.add(
            paramName = DigitaalAdresQueryParamNames.SOORTDIGITAALADRES.value,
            value = adresInformation.soortDigitaalAdres.toString()
        )
        query.add(
            paramName = DigitaalAdresQueryParamNames.REFERENTIE.value,
            value = adresInformation.referentie
        )

        return openKlantClient.getDigitaleAdressen(
            query = query,
            properties = properties
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
        val digitaleAdressen = openKlantClient.getDigitaleAdressen(
            query = DigitaalAdresQuery(
                queryParams = mutableMapOf(
                    DigitaalAdresQueryParamNames.HADBETROKKENE__WASPARTIJ__UUID.value to partij.uuidReference.toString()
                )
            ),
            properties = properties
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
                updateExistingPartij(
                    partij = partij,
                    contactInformation = contactInformation,
                    properties = properties
                )
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
