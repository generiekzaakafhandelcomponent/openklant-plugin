package com.ritense.valtimoplugins.openklant.dto

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.ritense.valtimoplugins.openklant.model.NestedUuid

@JsonInclude(JsonInclude.Include.NON_NULL)
data class PatchRekeningnummerRequest(
    @JsonProperty("iban")
    val iban: String? = null,
    @JsonProperty("partij")
    val partij: NestedUuid? = null,
    @JsonProperty("bic")
    val bic: String? = null,
)
