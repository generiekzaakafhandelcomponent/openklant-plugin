package com.ritense.valtimoplugins.openklant.model

import com.ritense.valtimoplugins.openklant.dto.FormioQueryParam
import mu.KotlinLogging

data class DigitaalAdresQuery(
    val queryParams: MutableMap<String, String> = mutableMapOf(),
) {
    fun add(
        paramName: String?,
        value: String?,
    ) {
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
        fun fromFormioList(queryParamList: List<FormioQueryParam>): DigitaalAdresQuery {
            val query = DigitaalAdresQuery()
            queryParamList
                .filter { it.key.isNotBlank() && it.value.isNotBlank() }
                .forEach { param ->
                    query.add(param.key.trim(), param.value.trim())
                }

            return query
        }

        private val logger = KotlinLogging.logger { }
    }
}
