package com.ritense.valtimoplugins.openklant.model

import com.ritense.valtimoplugins.openklant.model.KlantcontactQueryParamNames.HADBETROKKENE__WASPARTIJ__PARTIJIDENTIFICATOR__OBJECTID
import com.ritense.valtimoplugins.openklant.model.KlantcontactQueryParamNames.HADBETROKKENE__WASPARTIJ__UUID
import com.ritense.valtimoplugins.openklant.model.KlantcontactQueryParamNames.ONDERWERPOBJECT__ONDERWERPOBJECTIDENTIFICATORCODEOBJECTTYPE
import com.ritense.valtimoplugins.openklant.model.KlantcontactQueryParamNames.ONDERWERPOBJECT__ONDERWERPOBJECTIDENTIFICATOROBJECTID

// TODO: Organize KlantcontactQueries in the same way as DigitaalAdresQueries
data class KlantcontactQuery(
    val objectTypeId: String? = null,
    val objectUuid: String? = null,
    val bsn: String? = null,
    val partijUuid: String? = null,
) {
    /** The filled-in fields as Open Klant filters, in a fixed order; blank fields are left out. */
    fun toOpenKlantQuery(): OpenKlantQuery {
        val filters =
            listOf(
                ONDERWERPOBJECT__ONDERWERPOBJECTIDENTIFICATORCODEOBJECTTYPE to objectTypeId,
                HADBETROKKENE__WASPARTIJ__PARTIJIDENTIFICATOR__OBJECTID to bsn,
                ONDERWERPOBJECT__ONDERWERPOBJECTIDENTIFICATOROBJECTID to objectUuid,
                HADBETROKKENE__WASPARTIJ__UUID to partijUuid,
            )
        val queryParams = mutableMapOf<String, String>()
        filters.forEach { (name, value) ->
            if (!value.isNullOrBlank()) {
                queryParams[name.value] = value
            }
        }
        return OpenKlantQuery(queryParams)
    }
}
