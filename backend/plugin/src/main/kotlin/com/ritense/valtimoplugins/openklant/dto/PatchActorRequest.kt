package com.ritense.valtimoplugins.openklant.dto

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

@JsonInclude(JsonInclude.Include.NON_NULL)
data class PatchActorRequest(
    @JsonProperty("naam")
    val naam: String? = null,
    @JsonProperty("soortActor")
    val soortActor: Actor.SoortActor? = null,
    @JsonProperty("indicatieActief")
    val indicatieActief: Boolean? = null,
    @JsonProperty("actoridentificator")
    val actoridentificator: Identificator? = null,
    @JsonProperty("actorIdentificatie")
    val actorIdentificatie: Actor.ActorIdentificatie? = null,
)
