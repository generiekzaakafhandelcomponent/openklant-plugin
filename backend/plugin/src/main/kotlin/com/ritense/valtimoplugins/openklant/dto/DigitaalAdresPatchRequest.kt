package com.ritense.valtimoplugins.openklant.dto

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

@JsonInclude(JsonInclude.Include.NON_NULL) // Will only include non-null values in patch request
data class DigitaalAdresPatchRequest(
    @JsonProperty("verstrektDoorBetrokkene")
    val verstrektDoorBetrokkene: UuidReference? = null,
    @JsonProperty("verstrektDoorPartij")
    val verstrektDoorPartij: UuidReference? = null,
    @JsonProperty("adres")
    val adres: String? = null,
    @JsonProperty("soortDigitaalAdres")
    val soortDigitaalAdres: SoortDigitaalAdres? = null,
    @JsonProperty("isStandaardAdres")
    val isStandaardAdres: Boolean? = null,
    @JsonProperty("omschrijving")
    val omschrijving: String? = null,
    @JsonProperty("referentie")
    val referentie: String? = null,
    @JsonProperty("verificatieDatum")
    val verificatieDatum: String? = null,
)
