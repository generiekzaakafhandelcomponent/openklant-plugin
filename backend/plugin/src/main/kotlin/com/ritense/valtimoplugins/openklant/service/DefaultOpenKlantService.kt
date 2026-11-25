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
import com.ritense.valtimoplugins.openklant.model.KvkIdentificator
import com.ritense.valtimoplugins.openklant.model.NestedUuid
import com.ritense.valtimoplugins.openklant.model.OpenKlantProperties
import com.ritense.valtimoplugins.openklant.model.PartijInformation
import com.ritense.valtimoplugins.openklant.model.SoortDigitaalAdres
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

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
    ): Partij {
        require(partijInformation.bsn.isNotBlank()) { "A BSN is required to get or create a partij" }
        return getOrCreate(
            find = { openKlantClient.getPartijByBsn(partijInformation.bsn, properties) },
            create = { createNewPartij(partijInformation, properties) },
        )
    }

    override fun getOrCreateOrganisatie(
        kvkNummer: String,
        naam: String?,
        properties: OpenKlantProperties,
    ): Partij {
        require(KvkIdentificator.FORMAT.matches(kvkNummer)) {
            "A KVK-nummer consists of 8 digits, but was '$kvkNummer'"
        }
        return getOrCreate(
            find = { openKlantClient.getPartijByKvkNummer(kvkNummer, properties) },
            create = { openKlantClient.createPartij(partijFactory.createOrganisatie(kvkNummer, naam), properties) },
        )
    }

    /**
     * Two processes can look up the same partij at the same time and both try to create it. Open Klant keeps a
     * partij-identificator unique, so the second create fails: with 400 when Open Klant's own check sees the
     * identificator, or with 500 when the two inserts collide in the database. The partij the first one created is
     * then returned. Any other failure, or a failed second lookup, ends in the error of the create.
     */
    private fun getOrCreate(
        find: () -> Partij?,
        create: () -> Partij,
    ): Partij {
        find()?.let { return it }
        return try {
            create()
        } catch (e: ResponseStatusException) {
            if (e.statusCode.value() !in CONCURRENT_CREATE_STATUSES) {
                throw e
            }
            val existing =
                try {
                    find()
                } catch (lookupError: RuntimeException) {
                    e.addSuppressed(lookupError)
                    throw e
                } ?: throw e
            logger.info { "Partij was created concurrently, using the existing one (uuid: '${existing.uuid}')" }
            existing
        }
    }

    override fun getAllDigitaleAdressen(
        query: DigitaalAdresQuery,
        properties: OpenKlantProperties,
    ): List<DigitaalAdres> =
        openKlantClient
            .getDigitaleAdressen(
                query = query,
                properties = properties,
            ).map { it.toModel() }

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
    ): List<Klantcontact> = openKlantClient.getAllKlantcontacten(query, properties)

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

        val partij =
            requireNotNull(adresInformation.verstrektDoorPartij) {
                "Cannot clear the referentie of the current digitale adressen without a partij"
            }
        query.add(
            paramName = DigitaalAdresQueryParamNames.VERSTREKTDOORPARTIJ_UUID.value,
            value = partij.toString(),
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
                )

        val digitaleUniekeReferenties =
            digitaleAdressen.map {
                "${it.verstrektDoorPartij?.uuid},${it.referentie},${it.soortDigitaalAdres}"
            }

        if ("${partij.uuid},${contactInformation.zaaknummer},${SoortDigitaalAdres.EMAIL}" !in
            digitaleUniekeReferenties
        ) {
            createDigitalAddress(partij, contactInformation, properties)
        }
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
            createDigitalAddress(nieuwePartij, contactInformation, properties)
            nieuwePartij.uuid.toString()
        }
    }

    companion object {
        private val CONCURRENT_CREATE_STATUSES =
            setOf(HttpStatus.BAD_REQUEST.value(), HttpStatus.INTERNAL_SERVER_ERROR.value())
        private val logger = KotlinLogging.logger { }
    }
}
