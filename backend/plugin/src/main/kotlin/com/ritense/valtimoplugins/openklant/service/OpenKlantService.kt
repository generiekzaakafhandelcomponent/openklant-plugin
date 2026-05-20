package com.ritense.valtimoplugins.openklant.service

import com.ritense.valtimoplugins.openklant.dto.DigitaalAdresCreationRequest
import com.ritense.valtimoplugins.openklant.dto.DigitaalAdresPatchRequest
import com.ritense.valtimoplugins.openklant.dto.DigitaalAdres
import com.ritense.valtimoplugins.openklant.dto.Klantcontact
import com.ritense.valtimoplugins.openklant.dto.Partij
import com.ritense.valtimoplugins.openklant.dto.UuidReference
import com.ritense.valtimoplugins.openklant.model.ContactInformation
import com.ritense.valtimoplugins.openklant.model.DigitaalAdresQuery
import com.ritense.valtimoplugins.openklant.model.KlantcontactCreationInformation
import com.ritense.valtimoplugins.openklant.model.KlantcontactOptions
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

    fun getAllDigitaleAdressen(
        query: DigitaalAdresQuery,
        properties: OpenKlantProperties
    ): List<DigitaalAdres>

    fun setDefaultDigitaalAdres(
        digitaalAdresCreationRequest: DigitaalAdresCreationRequest,
        properties: OpenKlantProperties,
    ): DigitaalAdres

    fun updateDigitaalAdres(
        digitaalAdresUuid: UuidReference,
        digitaalAdresPatchRequest: DigitaalAdresPatchRequest,
        properties: OpenKlantProperties,
    ): DigitaalAdres

    fun getAllKlantcontacten(properties: KlantcontactOptions): List<Klantcontact>

    fun postKlantcontact(
        klantcontactCreationInformation: KlantcontactCreationInformation,
        properties: OpenKlantProperties,
    )
}
