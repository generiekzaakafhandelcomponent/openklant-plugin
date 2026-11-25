package com.ritense.valtimoplugins.openklant.service

import com.ritense.valtimoplugins.openklant.dto.Klantcontact
import com.ritense.valtimoplugins.openklant.dto.Partij
import com.ritense.valtimoplugins.openklant.model.ContactInformation
import com.ritense.valtimoplugins.openklant.model.DigitaalAdres
import com.ritense.valtimoplugins.openklant.model.DigitaalAdresPatch
import com.ritense.valtimoplugins.openklant.model.DigitaalAdresQuery
import com.ritense.valtimoplugins.openklant.model.GetOrCreateResult
import com.ritense.valtimoplugins.openklant.model.KlantcontactCreationInformation
import com.ritense.valtimoplugins.openklant.model.KlantcontactQuery
import com.ritense.valtimoplugins.openklant.model.NestedUuid
import com.ritense.valtimoplugins.openklant.model.OpenKlantProperties
import com.ritense.valtimoplugins.openklant.model.PartijInformation

interface OpenKlantService {
    fun storeContactInformation(
        contactInformation: ContactInformation,
        properties: OpenKlantProperties,
    ): String

    fun getOrCreatePartij(
        partijInformation: PartijInformation,
        properties: OpenKlantProperties,
    ): Partij

    /**
     * Finds the partij with this KVK-nummer (usually an organisatie, but Open Klant also accepts it on another soort
     * partij), or creates an organisatie with [naam] when there is none yet. Has a default so that other
     * implementations of this interface keep compiling.
     */
    fun getOrCreateOrganisatie(
        kvkNummer: String,
        naam: String?,
        properties: OpenKlantProperties,
    ): Partij = throw UnsupportedOperationException("getOrCreateOrganisatie is not implemented by ${javaClass.name}")

    fun getAllDigitaleAdressen(
        query: DigitaalAdresQuery,
        properties: OpenKlantProperties,
    ): List<DigitaalAdres>

    fun createDigitaalAdres(
        request: DigitaalAdres,
        properties: OpenKlantProperties,
    ): DigitaalAdres

    /**
     * Returns the adres of the same soort that the partij (or, without a partij, the betrokkene) already has and
     * that matches after normalising, or creates it. A new telefoonnummer is stored normalised, as Open Klant does
     * not accept spaces or dashes in it; e-mail and overig are stored as entered. Has a default so that other
     * implementations of this interface keep compiling.
     */
    fun getOrCreateDigitaalAdres(
        request: DigitaalAdres,
        properties: OpenKlantProperties,
    ): GetOrCreateResult<DigitaalAdres> =
        throw UnsupportedOperationException("getOrCreateDigitaalAdres is not implemented by ${javaClass.name}")

    fun setDefaultDigitaalAdres(
        request: DigitaalAdres,
        properties: OpenKlantProperties,
    ): DigitaalAdres

    fun updateDigitaalAdres(
        digitaalAdresUuid: NestedUuid,
        request: DigitaalAdresPatch,
        properties: OpenKlantProperties,
    ): DigitaalAdres

    fun getAllKlantcontacten(
        query: KlantcontactQuery,
        properties: OpenKlantProperties,
    ): List<Klantcontact>

    fun postKlantcontact(
        klantcontactCreationInformation: KlantcontactCreationInformation,
        properties: OpenKlantProperties,
    )
}
