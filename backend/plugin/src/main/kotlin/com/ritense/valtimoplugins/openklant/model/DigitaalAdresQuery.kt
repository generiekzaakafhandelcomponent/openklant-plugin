package com.ritense.valtimoplugins.openklant.model

data class DigitaalAdresQuery(
    val queryParams: MutableMap<String, String> = mutableMapOf()
) {

    fun add(paramName: String?, value: String?) {
        if (paramName.isNullOrBlank() || value.isNullOrBlank()) return

        if (queryParams.containsKey(paramName)) {
            throw IllegalArgumentException("Duplicate filter key: '$paramName'")
        }

        queryParams[paramName] = value
    }
}