package com.ritense.valtimoplugins.openklant.model

data class DigitaalAdresQuery(
    val queryParams: MutableMap<String, String> = mutableMapOf(),
) {
    fun add(
        paramName: String?,
        value: String?,
    ) = QueryParamSupport.add(queryParams, paramName, value)

    companion object {
        fun fromKeyValueQueryParamList(queryParamList: List<KeyValueQueryParam>): DigitaalAdresQuery =
            DigitaalAdresQuery(QueryParamSupport.toMap(queryParamList))
    }
}
