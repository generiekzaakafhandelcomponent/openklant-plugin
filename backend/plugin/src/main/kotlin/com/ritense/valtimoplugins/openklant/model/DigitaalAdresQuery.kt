package com.ritense.valtimoplugins.openklant.model

import mu.KotlinLogging

data class DigitaalAdresQuery(
    val queryParams: MutableMap<String, String> = mutableMapOf()
) {

    fun add(paramName: String?, value: String?) {
        if (paramName.isNullOrBlank()) {
            logger.warn { "Did not add paramName-value pair to queryParams: paramName is null or blank" }
            return
        }

        if (value.isNullOrBlank()) {
            logger.warn { "Did not add paramName-value pair to queryParams: value for '$paramName' is null or blank" }
            return
        }

        if (queryParams.containsKey(paramName)) {
            throw IllegalArgumentException("Duplicate filter key: '$paramName'")
        }

        queryParams[paramName] = value
    }

    companion object {
        private val logger = KotlinLogging.logger { }
    }
}