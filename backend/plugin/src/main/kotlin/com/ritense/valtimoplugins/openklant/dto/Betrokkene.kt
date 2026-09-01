package com.ritense.valtimoplugins.openklant.dto

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonValue
import java.util.UUID

data class Betrokkene(
    @JsonProperty("uuid")
    override val uuid: UUID,
    @JsonProperty("url")
    override val url: String,
    @JsonProperty("wasPartij")
    val wasPartij: UuidAndUrlReference? = null,
    @JsonProperty("hadKlantcontact")
    val hadKlantcontact: UuidAndUrlReference,
    @JsonProperty("digitaleAdressen")
    val digitaleAdressen: List<UuidAndUrlReference>,
    @JsonProperty("bezoekadres")
    val bezoekadres: Adres? = null,
    @JsonProperty("correspondentieadres")
    val correspondentieadres: Adres? = null,
    @JsonProperty("contactnaam")
    val contactnaam: Contactnaam? = null,
    @JsonProperty("volledigeNaam")
    val volledigeNaam: String,
    @JsonProperty("rol")
    val rol: Rol,
    @JsonProperty("organisatienaam")
    val organisatienaam: String? = null,
    @JsonProperty("initiator")
    val initiator: Boolean,
) : Referable {
    enum class Rol(
        val value: String,
    ) {
        VERTEGENWOORDIGER("vertegenwoordiger"),
        KLANT("klant"),
        ;

        @JsonValue
        fun toJson() = value

        companion object {
            @JvmStatic
            @JsonCreator
            fun fromValue(value: String): Rol =
                entries.firstOrNull { it.value.equals(value.trim(), ignoreCase = true) }
                    ?: throw IllegalArgumentException(
                        "Unknown rol '$value'. Supported values: ${entries.joinToString { it.value }}",
                    )
        }
    }
}
