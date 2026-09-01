package com.ritense.valtimoplugins.openklant.dto

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.ritense.valtimoplugins.openklant.model.NestedUuid

@JsonInclude(JsonInclude.Include.NON_NULL)
data class CreateInterneTaakRequest(
    @JsonProperty("gevraagdeHandeling")
    val gevraagdeHandeling: String,
    @JsonProperty("aanleidinggevendKlantcontact")
    val aanleidinggevendKlantcontact: NestedUuid,
    @JsonProperty("status")
    val status: InterneTaakStatus,
    @JsonProperty("nummer")
    val nummer: String? = null,
    @JsonProperty("referentienummer")
    val referentienummer: String? = null,
    @JsonProperty("toegewezenAanActoren")
    val toegewezenAanActoren: List<NestedUuid>? = null,
    @JsonProperty("toelichting")
    val toelichting: String? = null,
    @JsonProperty("afgehandeldOp")
    val afgehandeldOp: String? = null,
)
