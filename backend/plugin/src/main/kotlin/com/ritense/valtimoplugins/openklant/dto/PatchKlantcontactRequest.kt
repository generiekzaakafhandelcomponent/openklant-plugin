package com.ritense.valtimoplugins.openklant.dto

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

@JsonInclude(JsonInclude.Include.NON_NULL)
data class PatchKlantcontactRequest(
    @JsonProperty("kanaal")
    val kanaal: String? = null,
    @JsonProperty("onderwerp")
    val onderwerp: String? = null,
    @JsonProperty("taal")
    val taal: String? = null,
    @JsonProperty("vertrouwelijk")
    val vertrouwelijk: Boolean? = null,
    @JsonProperty("nummer")
    val nummer: String? = null,
    @JsonProperty("referentienummer")
    val referentienummer: String? = null,
    @JsonProperty("inhoud")
    val inhoud: String? = null,
    @JsonProperty("reactie")
    val reactie: String? = null,
    @JsonProperty("indicatieContactGelukt")
    val indicatieContactGelukt: Boolean? = null,
    @JsonProperty("plaatsgevondenOp")
    val plaatsgevondenOp: String? = null,
    @JsonProperty("metadata")
    val metadata: Map<String, String>? = null,
)
