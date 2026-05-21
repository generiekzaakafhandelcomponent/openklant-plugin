package com.ritense.valtimoplugins.openklant.dto

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonValue
import jakarta.validation.constraints.NotBlank
import java.util.UUID

data class UuidReference(
    val uuid: UUID,
) {
    @JsonValue
    override fun toString(): String {
        return uuid.toString()
    }

    companion object {
        @JvmStatic // Recommended for older versions of Jackson to be able to discover this method in a companion object
        @JsonCreator
        fun fromString(uuidString: String): UuidReference{
            val uuid = UUID.fromString(uuidString)
            return UuidReference(uuid)
        }
    }
}
