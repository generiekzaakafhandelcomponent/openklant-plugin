package com.ritense.valtimoplugins.openklant.dto

import java.util.UUID

// Should serialize to an object with one nested UUID key-value pair, e.g.:
// {
//     "uuid": "7d0ce1fb-53aa-48bd-be8b-a16727b78843"
// }
// This is different from a 'normal' UUID, which gets serialized like this:
// "uuid": "7d0ce1fb-53aa-48bd-be8b-a16727b78843"
// So NOT in an object.

data class UuidReference(
    val uuid: UUID,
) {
    override fun toString(): String = uuid.toString()

    companion object {
        fun fromString(uuidString: String): UuidReference {
            val uuid = UUID.fromString(uuidString)
            return UuidReference(uuid)
        }
    }
}
