package com.ritense.valtimoplugins.openklant.dto

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.ritense.valtimoplugins.openklant.model.NestedUuid
import com.ritense.valtimoplugins.openklant.model.SoortDigitaalAdres

@JsonInclude(JsonInclude.Include.NON_EMPTY)
data class DigitaalAdresCreationRequest(
    @JsonProperty("verstrektDoorBetrokkene")
    val verstrektDoorBetrokkene: NestedUuid? = null,
    // Always sent, also as null: Open Klant 2.19 requires the key because of its unique constraint on
    // partij and soortDigitaalAdres, and answers 400 when an adres of only a betrokkene leaves it out
    @JsonInclude(JsonInclude.Include.ALWAYS)
    @JsonProperty("verstrektDoorPartij")
    val verstrektDoorPartij: NestedUuid? = null,
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
