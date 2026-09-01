package com.ritense.valtimoplugins.openklant.dto

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.ritense.valtimoplugins.openklant.model.NestedUuid

@JsonInclude(JsonInclude.Include.NON_NULL)
data class PatchOnderwerpobjectRequest(
    @JsonProperty("klantcontact")
    val klantcontact: NestedUuid? = null,
    @JsonProperty("wasKlantcontact")
    val wasKlantcontact: NestedUuid? = null,
    @JsonProperty("onderwerpobjectidentificator")
    val onderwerpobjectidentificator: Onderwerpobjectidentificator? = null,
)
