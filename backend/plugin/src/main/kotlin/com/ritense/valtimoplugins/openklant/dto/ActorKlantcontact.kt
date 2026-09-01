package com.ritense.valtimoplugins.openklant.dto

import com.fasterxml.jackson.annotation.JsonProperty
import java.util.UUID

data class ActorKlantcontact(
    @JsonProperty("uuid")
    override val uuid: UUID,
    @JsonProperty("url")
    override val url: String,
    @JsonProperty("actor")
    val actor: UuidAndUrlReference,
    @JsonProperty("klantcontact")
    val klantcontact: UuidAndUrlReference,
) : Referable
