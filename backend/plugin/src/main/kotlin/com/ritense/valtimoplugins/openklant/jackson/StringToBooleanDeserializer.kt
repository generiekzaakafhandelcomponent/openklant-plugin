package com.ritense.valtimoplugins.openklant.jackson

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer

// Also known as the FormioDeStupidifier
class StringToBooleanDeserializer : JsonDeserializer<Boolean?>() {

    override fun deserialize(
        p: JsonParser,
        ctxt: DeserializationContext
    ): Boolean? {
        return when (p.valueAsString?.trim()?.lowercase()) {
            "true" -> true
            "false" -> false
            "", "null", null -> null
            else -> null
        }
    }
}