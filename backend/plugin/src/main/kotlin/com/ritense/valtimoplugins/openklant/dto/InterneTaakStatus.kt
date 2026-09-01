package com.ritense.valtimoplugins.openklant.dto

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue

enum class InterneTaakStatus(
    val value: String,
) {
    TE_VERWERKEN("te_verwerken"),
    VERWERKT("verwerkt"),
    ;

    @JsonValue
    fun toJson() = value

    companion object {
        @JvmStatic
        @JsonCreator
        fun fromValue(value: String): InterneTaakStatus =
            entries.firstOrNull { it.value.equals(value.trim(), ignoreCase = true) }
                ?: throw IllegalArgumentException(
                    "Unknown interne taak status '$value'. Supported values: ${entries.joinToString { it.value }}",
                )
    }
}
