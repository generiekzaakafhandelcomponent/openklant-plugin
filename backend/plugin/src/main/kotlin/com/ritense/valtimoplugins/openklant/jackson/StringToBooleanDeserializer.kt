package com.ritense.valtimoplugins.openklant.jackson

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer

class StringToBooleanDeserializer : JsonDeserializer<Boolean?>() {
    override fun deserialize(
        p: JsonParser,
        ctxt: DeserializationContext,
    ): Boolean? =
        when (p.valueAsString?.trim()?.lowercase()) {
            "true" -> true
            "false" -> false
            "", "null", null -> null
            else -> null
        }
}
