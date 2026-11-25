package com.ritense.valtimoplugins.openklant.model

/** How Open Klant identifies an organisatie by its KVK-nummer: a partij-identificator from the Handelsregister. */
object KvkIdentificator {
    const val CODE_OBJECTTYPE = "niet_natuurlijk_persoon"
    const val CODE_REGISTER = "hr"
    const val CODE_SOORT_OBJECT_ID = "kvk_nummer"

    /** A KVK-nummer consists of 8 digits. */
    val FORMAT = Regex("^[0-9]{8}$")
}
