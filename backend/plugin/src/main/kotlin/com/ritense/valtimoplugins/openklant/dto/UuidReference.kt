package com.ritense.valtimoplugins.openklant.dto

import java.util.UUID

data class UuidReference(
    val uuid: UUID,
) {
    override fun toString(): String {
        return uuid.toString()
    }
}
