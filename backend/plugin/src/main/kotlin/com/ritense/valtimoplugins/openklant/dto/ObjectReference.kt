package com.ritense.valtimoplugins.openklant.dto

import com.fasterxml.jackson.annotation.JsonProperty
import java.util.UUID

data class ObjectReference(
    @JsonProperty("uuid")
    val uuid: UUID,
    @JsonProperty("url")
    val url: String,
)
