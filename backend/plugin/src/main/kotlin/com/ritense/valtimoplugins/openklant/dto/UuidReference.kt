package com.ritense.valtimoplugins.openklant.dto

import java.util.UUID

// Should serialize to an object with one UUID key-value pair, e.g.:
// {
//     "uuid": "7d0ce1fb-53aa-48bd-be8b-a16727b78843"
// }
data class UuidReference(
    val uuid: UUID,
) {
    override fun toString(): String {
        return uuid.toString()
    }

    companion object {
        fun fromString(uuidString: String): com.ritense.valtimoplugins.openklant.dto.UuidReference {
            val uuid = UUID.fromString(uuidString)
            return UuidReference(uuid)
        }
    }
}
