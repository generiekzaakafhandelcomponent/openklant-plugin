package com.ritense.valtimoplugins.openklant.model

/** The outcome of a get-or-create: the object, and whether it was created by this call. */
data class GetOrCreateResult<T>(
    val value: T,
    val created: Boolean,
)
