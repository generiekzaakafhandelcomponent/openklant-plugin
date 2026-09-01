package com.ritense.valtimoplugins.openklant.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue

enum class SoortDigitaalAdres(
    val value: String,
) {
    EMAIL("email"),
    TELEFOONNUMMER("telefoonnummer"),
    OVERIG("overig"),
    ;

    @JsonValue
    fun toJson() = value

    companion object {
        @JvmStatic
        @JsonCreator
        fun fromValue(value: String): SoortDigitaalAdres =
            entries.firstOrNull { it.value.equals(value.trim(), ignoreCase = true) }
                ?: throw IllegalArgumentException(
                    "Unknown soortDigitaalAdres '$value'. Supported values: ${entries.joinToString { it.value }}",
                )
    }
}
