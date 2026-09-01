package com.ritense.valtimoplugins.openklant.dto

import com.fasterxml.jackson.annotation.JsonProperty
import java.util.UUID

data class Bijlage(
    @JsonProperty("uuid")
    override val uuid: UUID,
    @JsonProperty("url")
    override val url: String,
    @JsonProperty("wasBijlageVanKlantcontact")
    val wasBijlageVanKlantcontact: UuidAndUrlReference? = null,
    @JsonProperty("bijlageidentificator")
    val bijlageidentificator: BijlageIdentificator? = null,
) : Referable
