package com.ritense.valtimoplugins.openklant.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class UuidAndUrlReference(
    @JsonProperty("uuid")
    override val uuidReference: UuidReference,
    @JsonProperty("url")
    override val url: String,
) : Referable
