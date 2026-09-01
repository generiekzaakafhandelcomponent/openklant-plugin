package com.ritense.valtimoplugins.openklant.dto

import com.fasterxml.jackson.annotation.JsonProperty
import java.util.UUID

data class Rekeningnummer(
    @JsonProperty("uuid")
    override val uuid: UUID,
    @JsonProperty("url")
    override val url: String,
    @JsonProperty("partij")
    val partij: UuidAndUrlReference? = null,
    @JsonProperty("iban")
    val iban: String,
    @JsonProperty("bic")
    val bic: String? = null,
) : Referable
