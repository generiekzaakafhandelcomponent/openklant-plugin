package com.ritense.valtimoplugins.openklant.dto

interface Referable {
    val uuid: String
    val url: String

    fun getObjectReference(): ObjectReference = ObjectReference(uuid, url)

<<<<<<< ours
    fun makeUuidReference(): UuidReference = UuidReference(uuid)
=======
    fun getUuidReference(): UuidReference = UuidReference(uuid)
>>>>>>> theirs
}
