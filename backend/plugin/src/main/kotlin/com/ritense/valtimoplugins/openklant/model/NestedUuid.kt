package com.ritense.valtimoplugins.openklant.model

import java.util.UUID

data class NestedUuid(
    val uuid: UUID,
) {
    override fun toString(): String = uuid.toString()

    companion object {
        fun fromString(uuidString: String): NestedUuid {
            val uuid = UUID.fromString(uuidString)
            return NestedUuid(uuid)
        }
    }
}
