package com.ritense.valtimoplugins.openklant.dto

import java.util.UUID

interface Referable {
    val uuid: UUID
    val url: String
}
