package com.ritense.valtimoplugins.openklant.util

import com.ritense.valtimoplugins.openklant.model.NestedUuid
import java.util.UUID

// Conversions for @PluginActionProperty values, which process links always store as strings.

fun String?.toRequiredUuid(propertyName: String): UUID =
    requireNotNull(toUuidIfPresent()) {
        "Action property '$propertyName' is required and must contain a UUID"
    }

fun String?.toNestedUuidIfPresent(): NestedUuid? = toUuidIfPresent()?.let { NestedUuid(it) }

fun String?.toRequiredNestedUuid(propertyName: String): NestedUuid = NestedUuid(toRequiredUuid(propertyName))

/** Reads a comma-separated list of UUIDs, as used for the to-many references of the klantinteracties API. */
fun String?.toNestedUuidList(): List<NestedUuid>? =
    trimToNull()
        ?.split(",")
        ?.mapNotNull { it.toNestedUuidIfPresent() }

fun String?.toRequiredString(propertyName: String): String =
    requireNotNull(trimToNull()) {
        "Action property '$propertyName' is required"
    }
