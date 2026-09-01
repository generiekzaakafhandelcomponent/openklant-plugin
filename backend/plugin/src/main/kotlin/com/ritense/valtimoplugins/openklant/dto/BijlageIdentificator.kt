package com.ritense.valtimoplugins.openklant.dto

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

@JsonInclude(JsonInclude.Include.NON_NULL)
data class BijlageIdentificator(
    @JsonProperty("objectId")
    val objectId: String? = null,
    @JsonProperty("codeObjecttype")
    val codeObjecttype: String? = null,
    @JsonProperty("codeRegister")
    val codeRegister: String? = null,
    @JsonProperty("codeSoortObjectId")
    val codeSoortObjectId: String? = null,
)
