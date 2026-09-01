package com.ritense.valtimoplugins.openklant.dto

import com.fasterxml.jackson.annotation.JsonProperty
import java.util.UUID

data class InterneTaak(
    @JsonProperty("uuid")
    override val uuid: UUID,
    @JsonProperty("url")
    override val url: String,
    @JsonProperty("nummer")
    val nummer: String? = null,
    @JsonProperty("referentienummer")
    val referentienummer: String? = null,
    @JsonProperty("gevraagdeHandeling")
    val gevraagdeHandeling: String,
    @JsonProperty("aanleidinggevendKlantcontact")
    val aanleidinggevendKlantcontact: UuidAndUrlReference,
    @JsonProperty("toegewezenAanActor")
    val toegewezenAanActor: UuidAndUrlReference? = null,
    @JsonProperty("toegewezenAanActoren")
    val toegewezenAanActoren: List<UuidAndUrlReference> = emptyList(),
    @JsonProperty("toelichting")
    val toelichting: String? = null,
    @JsonProperty("status")
    val status: InterneTaakStatus,
    @JsonProperty("toegewezenOp")
    val toegewezenOp: String? = null,
    @JsonProperty("afgehandeldOp")
    val afgehandeldOp: String? = null,
) : Referable
