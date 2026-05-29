package com.ritense.valtimoplugins.openklant.service

import com.ritense.valtimoplugins.openklant.client.OpenKlantClient
import com.ritense.valtimoplugins.openklant.dto.DigitaalAdresCreationRequest
import com.ritense.valtimoplugins.openklant.dto.DigitaalAdresPatchRequest
import com.ritense.valtimoplugins.openklant.dto.DigitaalAdresResponse
import com.ritense.valtimoplugins.openklant.dto.Klantcontact
import com.ritense.valtimoplugins.openklant.dto.Partij
import com.ritense.valtimoplugins.openklant.mapper.toCreationRequest
import com.ritense.valtimoplugins.openklant.mapper.toModel
import com.ritense.valtimoplugins.openklant.mapper.toRequest
import com.ritense.valtimoplugins.openklant.model.ContactInformation
import com.ritense.valtimoplugins.openklant.model.DigitaalAdres
import com.ritense.valtimoplugins.openklant.model.DigitaalAdresPatch
import com.ritense.valtimoplugins.openklant.model.DigitaalAdresQuery
import com.ritense.valtimoplugins.openklant.model.DigitaalAdresQueryParamNames
import com.ritense.valtimoplugins.openklant.model.KlantcontactCreationInformation
import com.ritense.valtimoplugins.openklant.model.KlantcontactQuery
import com.ritense.valtimoplugins.openklant.model.NestedUuid
import com.ritense.valtimoplugins.openklant.model.OpenKlantProperties
import com.ritense.valtimoplugins.openklant.model.PartijInformation
import com.ritense.valtimoplugins.openklant.model.SoortDigitaalAdres
import org.springframework.stereotype.Service

@Service
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
                    properties = properties,
                )
            ) {
                updateExistingPartij(
                    partij = partij,
                    contactInformation = contactInformation,
                    properties = properties,
                )
            }
            partij.uuid.toString()
        } else {
            createAndStoreNewPartij(contactInformation, properties)
        }
    }

    override fun getOrCreatePartij(
        partijInformation: PartijInformation,
        properties: OpenKlantProperties,
    ): Partij =
        openKlantClient.getPartijByBsn(partijInformation.bsn, properties) ?: createNewPartij(
            partijInformation,
            properties,
        )

    override fun getAllDigitaleAdressen(
        query: DigitaalAdresQuery,
        properties: OpenKlantProperties,
    ): List<DigitaalAdres> =
        openKlantClient
            .getDigitaleAdressen(
                query = query,
                properties = properties,
            ).map { it -> it.toModel() }

    override fun createDigitaalAdres(
        request: DigitaalAdres,
        properties: OpenKlantProperties,
    ): DigitaalAdres =
        openKlantClient
            .createDigitaalAdres(
                request = request.toCreationRequest(),
                properties = properties,
            ).toModel()

    override fun setDefaultDigitaalAdres(
        request: DigitaalAdres,
        properties: OpenKlantProperties,
    ): DigitaalAdres {
        val creationRequest = request.toCreationRequest()

        clearReferentieForCurrentDigitaalAdressen(
            adresInformation = creationRequest,
            properties = properties,
        )

        return openKlantClient
            .createDigitaalAdres(
                request = creationRequest,
                properties = properties,
            ).toModel()
    }

    override fun updateDigitaalAdres(
        digitaalAdresUuid: NestedUuid,
        request: DigitaalAdresPatch,
        properties: OpenKlantProperties,
    ): DigitaalAdres =
        openKlantClient
            .updateDigitaalAdres(
                digitaalAdresUuid = digitaalAdresUuid,
                patchData = request.toRequest(),
                properties = properties,
            ).toModel()

    override fun getAllKlantcontacten(
        query: KlantcontactQuery,
        properties: OpenKlantProperties,
    ): List<Klantcontact> = openKlantClient.getKlantcontacten(query, properties).results

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

    private fun clearReferentieForCurrentDigitaalAdressen(
        adresInformation: DigitaalAdresCreationRequest,
        properties: OpenKlantProperties,
    ) {
        require(adresInformation.referentie != null) {
            "Cannot get DefaultAdressen without 'referentie'"
        }

        val query = DigitaalAdresQuery()

        query.add(
            paramName = DigitaalAdresQueryParamNames.VERSTREKTDOORPARTIJ_UUID.value,
            value = adresInformation.verstrektDoorPartij.toString(),
        )
        query.add(
            paramName = DigitaalAdresQueryParamNames.SOORTDIGITAALADRES.value,
            value = adresInformation.soortDigitaalAdres.value,
        )
        query.add(
            paramName = DigitaalAdresQueryParamNames.REFERENTIE.value,
            value = adresInformation.referentie,
        )

        openKlantClient
            .getDigitaleAdressen(
                query = query,
                properties = properties,
            ).forEach {
                openKlantClient.updateDigitaalAdres(
                    digitaalAdresUuid = NestedUuid(it.uuid),
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
        val voorkeursAdresObjectReference = partij.voorkeursDigitaalAdres ?: return false
        val voorkeursAdres =
            openKlantClient.getDigitaalAdres(
                NestedUuid(voorkeursAdresObjectReference.uuid),
                properties = properties,
            )
        return voorkeursAdres.adres == emailAddress
    }

    private fun createDigitalAddress(
        partij: Partij,
        contactInformation: ContactInformation,
        properties: OpenKlantProperties,
    ): DigitaalAdresResponse =
        openKlantClient.createDigitaalAdres(
            DigitaalAdresCreationRequest(
                verstrektDoorPartij = NestedUuid(partij.uuid),
                adres = contactInformation.emailadres,
                soortDigitaalAdres = SoortDigitaalAdres.EMAIL,
                referentie = contactInformation.zaaknummer,
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
        val digitaleAdressen =
            openKlantClient
                .getDigitaleAdressen(
                    query =
                        DigitaalAdresQuery(
                            queryParams =
                                mutableMapOf(
                                    DigitaalAdresQueryParamNames.VERSTREKTDOORPARTIJ_UUID.value to
                                        partij.uuid.toString(),
                                ),
                        ),
                    properties = properties,
                ).toMutableList()

        val digitaleUniekeReferenties =
            digitaleAdressen.map {
                "${it.verstrektDoorPartij?.uuid},${it.referentie},${it.soortDigitaalAdres}"
            }

        // Maak alleen nieuwe aan wanneer deze uniek is (niet bestaat)
        if ("${partij.uuid},${contactInformation.zaaknummer},${SoortDigitaalAdres.EMAIL}" !in
            digitaleUniekeReferenties
        ) {
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
                    properties = properties,
                )
            }
            partij.uuid.toString()
        } else {
            val nieuwePartij = createNewPartij(contactInformation, properties)
            val nieuweDigitaleAdress = createDigitalAddress(nieuwePartij, contactInformation, properties)

            updateDigitaleAdressenForPartij(nieuwePartij, nieuweDigitaleAdress, properties)
            return nieuwePartij.uuid.toString()
        }
    }

    private fun updateDigitaleAdressenForPartij(
        partij: Partij,
        digitaleAdressen: List<DigitaalAdresResponse>,
        properties: OpenKlantProperties,
    ) {
        val patchData =
            mapOf(
                "digitaleAdressen" to digitaleAdressen.map { it.uuid },
            )
        openKlantClient.patchPartij(partij.uuid.toString(), patchData, properties)
    }

    private fun updateDigitaleAdressenForPartij(
        partij: Partij,
        digitaleAdress: DigitaalAdresResponse,
        properties: OpenKlantProperties,
    ) = updateDigitaleAdressenForPartij(partij, listOf(digitaleAdress), properties)
}
