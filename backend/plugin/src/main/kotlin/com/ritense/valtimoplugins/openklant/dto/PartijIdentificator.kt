package com.ritense.valtimoplugins.openklant.dto

import com.fasterxml.jackson.annotation.JsonProperty
import java.util.UUID

data class PartijIdentificator(
    @JsonProperty("uuid")
    override val uuid: UUID,
    @JsonProperty("url")
    override val url: String,
    @JsonProperty("identificeerdePartij")
    val identificeerdePartij: ObjectReference,
    @JsonProperty("partijIdentificator")
    val partijIdentificator: Identificator,
    @JsonProperty("subIdentificatorVan")
    val subIdentificatorVan: ObjectReference?,
) : Referable
