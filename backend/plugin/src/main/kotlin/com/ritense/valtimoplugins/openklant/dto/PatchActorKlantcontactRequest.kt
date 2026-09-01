package com.ritense.valtimoplugins.openklant.dto

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.ritense.valtimoplugins.openklant.model.NestedUuid

@JsonInclude(JsonInclude.Include.NON_NULL)
data class PatchActorKlantcontactRequest(
    @JsonProperty("actor")
    val actor: NestedUuid? = null,
    @JsonProperty("klantcontact")
    val klantcontact: NestedUuid? = null,
)
