package com.ritense.valtimoplugins.openklant.model

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonValue

enum class SoortDigitaalAdres(
    val value: String,
) {
    @JsonProperty("email")
    EMAIL("email"),

    @JsonProperty("telefoonnummer")
    TELEFOONNUMMER("telefoonnummer"),

    @JsonProperty("overig")
    OVERIG("overig"),
    ;

    @JsonValue
    fun toJson() = value
}
