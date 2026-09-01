package com.ritense.valtimoplugins.openklant.dto

import com.fasterxml.jackson.annotation.JsonProperty
import java.util.UUID

data class Onderwerpobject(
    @JsonProperty("uuid")
    override val uuid: UUID,
    @JsonProperty("url")
    override val url: String,
    @JsonProperty("klantcontact")
    val klantcontact: UuidAndUrlReference,
    @JsonProperty("wasKlantcontact")
    val wasKlantcontact: UuidAndUrlReference? = null,
    @JsonProperty("onderwerpobjectidentificator")
    val onderwerpobjectidentificator: Onderwerpobjectidentificator? = null,
) : Referable
