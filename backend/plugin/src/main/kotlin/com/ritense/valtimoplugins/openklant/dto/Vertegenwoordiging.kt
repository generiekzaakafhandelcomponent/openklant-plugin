package com.ritense.valtimoplugins.openklant.dto

import com.fasterxml.jackson.annotation.JsonProperty
import java.util.UUID

/** Maps the 'Vertegenwoordigden' resource exposed on the /vertegenwoordigingen endpoint. */
data class Vertegenwoordiging(
    @JsonProperty("uuid")
    override val uuid: UUID,
    @JsonProperty("url")
    override val url: String,
    @JsonProperty("vertegenwoordigendePartij")
    val vertegenwoordigendePartij: UuidAndUrlReference,
    @JsonProperty("vertegenwoordigdePartij")
    val vertegenwoordigdePartij: UuidAndUrlReference,
) : Referable
