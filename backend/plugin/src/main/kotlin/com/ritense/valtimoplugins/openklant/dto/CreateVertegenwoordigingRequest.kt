package com.ritense.valtimoplugins.openklant.dto

import com.fasterxml.jackson.annotation.JsonProperty
import com.ritense.valtimoplugins.openklant.model.NestedUuid

data class CreateVertegenwoordigingRequest(
    @JsonProperty("vertegenwoordigendePartij")
    val vertegenwoordigendePartij: NestedUuid,
    @JsonProperty("vertegenwoordigdePartij")
    val vertegenwoordigdePartij: NestedUuid,
)
