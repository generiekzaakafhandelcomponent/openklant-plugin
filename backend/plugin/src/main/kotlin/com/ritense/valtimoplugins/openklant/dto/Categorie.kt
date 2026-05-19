package com.ritense.valtimoplugins.openklant.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class Categorie(
    @JsonProperty("uuid")
    override val uuid: UuidReference,
    @JsonProperty("url")
    override val url: String,
    @JsonProperty("naam")
    val naam: String,
) : Referable
