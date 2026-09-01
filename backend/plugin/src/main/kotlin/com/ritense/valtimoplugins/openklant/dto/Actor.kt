package com.ritense.valtimoplugins.openklant.dto

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonValue
import java.util.UUID

data class Actor(
    @JsonProperty("uuid")
    override val uuid: UUID,
    @JsonProperty("url")
    override val url: String,
    @JsonProperty("naam")
    val naam: String,
    @JsonProperty("soortActor")
    val soortActor: SoortActor,
    @JsonProperty("indicatieActief")
    val indicatieActief: Boolean? = null,
    @JsonProperty("actoridentificator")
    val actoridentificator: Identificator? = null,
    @JsonProperty("actorIdentificatie")
    val actorIdentificatie: ActorIdentificatie? = null,
) : Referable {
    enum class SoortActor(
        val value: String,
    ) {
        MEDEWERKER("medewerker"),
        GEAUTOMATISEERDE_ACTOR("geautomatiseerde_actor"),
        ORGANISATORISCHE_EENHEID("organisatorische_eenheid"),
        ;

        @JsonValue
        fun toJson() = value

        companion object {
            @JvmStatic
            @JsonCreator
            fun fromValue(value: String): SoortActor =
                entries.firstOrNull { it.value.equals(value.trim(), ignoreCase = true) }
                    ?: throw IllegalArgumentException(
                        "Unknown soortActor '$value'. Supported values: ${entries.joinToString { it.value }}",
                    )
        }
    }

    /** Flattened union of the three 'actorIdentificatie' variants, which the API discriminates on 'soortActor'. */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    data class ActorIdentificatie(
        @JsonProperty("functie")
        val functie: String? = null,
        @JsonProperty("emailadres")
        val emailadres: String? = null,
        @JsonProperty("telefoonnummer")
        val telefoonnummer: String? = null,
        @JsonProperty("omschrijving")
        val omschrijving: String? = null,
        @JsonProperty("faxnummer")
        val faxnummer: String? = null,
    )
}
