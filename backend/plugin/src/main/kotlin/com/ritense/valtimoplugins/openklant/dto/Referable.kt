package com.ritense.valtimoplugins.openklant.dto

interface Referable {
    val uuidReference: UuidReference
    val url: String

    fun getObjectReference(): ObjectReference = ObjectReference(uuidReference.uuid, url)

    fun getUuidReference(): UuidReference = UuidReference(uuidReference.uuid)
}
