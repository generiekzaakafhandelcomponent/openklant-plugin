package com.ritense.valtimoplugins.openklant.model

import io.github.oshai.kotlinlogging.KotlinLogging

/** Query parameters for the paginated 'list' endpoints; parameter names are passed through verbatim. */
data class OpenKlantQuery(
    val queryParams: MutableMap<String, String> = mutableMapOf(),
) {
    fun add(
        paramName: String?,
        value: String?,
    ): OpenKlantQuery {
        QueryParamSupport.add(queryParams, paramName, value)
        return this
    }

    companion object {
        fun fromKeyValueQueryParamList(queryParamList: List<KeyValueQueryParam>): OpenKlantQuery =
            OpenKlantQuery(QueryParamSupport.toMap(queryParamList))

        fun of(vararg params: Pair<String, String?>): OpenKlantQuery =
            OpenKlantQuery().apply {
                params.forEach { (key, value) -> add(key, value) }
            }
    }
}

/** Shared parameter-collection behaviour for [OpenKlantQuery] and [DigitaalAdresQuery]. */
internal object QueryParamSupport {
    private val logger = KotlinLogging.logger { }

    fun add(
        queryParams: MutableMap<String, String>,
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

    fun toMap(queryParamList: List<KeyValueQueryParam>): MutableMap<String, String> {
        val queryParams = mutableMapOf<String, String>()
        queryParamList
            .filter { it.key.isNotBlank() && it.value.isNotBlank() }
            .forEach { add(queryParams, it.key.trim(), it.value.trim()) }
        return queryParams
    }
}
