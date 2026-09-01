package com.ritense.valtimoplugins.openklant.dto

import com.fasterxml.jackson.annotation.JsonProperty
import com.ritense.valtimoplugins.openklant.model.NestedUuid

data class CreateActorKlantcontactRequest(
    @JsonProperty("actor")
    val actor: NestedUuid,
    @JsonProperty("klantcontact")
    val klantcontact: NestedUuid,
)
