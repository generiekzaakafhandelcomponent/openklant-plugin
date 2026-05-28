package com.ritense.valtimoplugins.openklant.dto

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.ritense.valtimoplugins.openklant.model.NestedUuid
import com.ritense.valtimoplugins.openklant.model.SoortDigitaalAdres

@JsonInclude(JsonInclude.Include.NON_NULL)
data class DigitaalAdresCreationRequest(
    @JsonProperty("verstrektDoorBetrokkene")
    val verstrektDoorBetrokkene: NestedUuid? = null,
    @JsonProperty("verstrektDoorPartij")
    val verstrektDoorPartij: NestedUuid,
    @JsonProperty("adres")
    val adres: String,
    @JsonProperty("soortDigitaalAdres")
    val soortDigitaalAdres: SoortDigitaalAdres,
    @JsonProperty("isStandaardAdres")
    val isStandaardAdres: Boolean? = false,
    @JsonProperty("omschrijving")
    val omschrijving: String = "",
    @JsonProperty("referentie")
    val referentie: String?,
    @JsonProperty("verificatieDatum")
    val verificatieDatum: String? = null,
)
