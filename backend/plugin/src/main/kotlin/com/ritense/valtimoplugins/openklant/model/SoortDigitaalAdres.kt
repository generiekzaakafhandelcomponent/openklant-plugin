package com.ritense.valtimoplugins.openklant.model

import com.fasterxml.jackson.annotation.JsonValue

enum class SoortDigitaalAdres(
    val value: String,
) {
    EMAIL("email"),
    TELEFOONNUMMER("telefoonnummer"),
    OVERIG("overig");

    @JsonValue
    fun toJson() = value
}
