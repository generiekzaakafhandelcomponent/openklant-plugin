package com.ritense.valtimoplugins.openklant.util

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer

class StringToBooleanDeserializer : JsonDeserializer<Boolean?>() {
    override fun deserialize(
        parser: JsonParser,
        context: DeserializationContext,
    ): Boolean? =
        when (parser.valueAsString?.trim()?.lowercase()) {
            "true" -> true
            "false" -> false
            "", "null", null -> null
            else -> null
        }
}
