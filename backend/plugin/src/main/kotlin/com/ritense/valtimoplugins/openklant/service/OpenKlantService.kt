package com.ritense.valtimoplugins.openklant.service

import com.ritense.valtimoplugins.openklant.dto.Klantcontact
import com.ritense.valtimoplugins.openklant.dto.Partij
import com.ritense.valtimoplugins.openklant.model.ContactInformation
import com.ritense.valtimoplugins.openklant.model.KlantcontactCreationInformation
import com.ritense.valtimoplugins.openklant.model.KlantcontactOptions
import com.ritense.valtimoplugins.openklant.model.OpenKlantProperties
import com.ritense.valtimoplugins.openklant.model.PartijInformation

interface OpenKlantService {
    suspend fun storeContactInformation(
        properties: OpenKlantProperties,
        contactInformation: ContactInformation,
    ): String

    suspend fun getAllKlantContacten(properties: KlantContactOptions): List<KlantContact>
}
