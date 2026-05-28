package com.ritense.valtimoplugins.openklant.dto

import com.fasterxml.jackson.annotation.JsonProperty
import com.ritense.valtimoplugins.openklant.model.SoortDigitaalAdres
import java.util.UUID

data class DigitaalAdresResponse(
    @JsonProperty("uuid")
    override val uuid: UUID,
    @JsonProperty("url")
    override val url: String,
    @JsonProperty("verstrektDoorBetrokkene")
    val verstrektDoorBetrokkene: ObjectReference? = null,
    @JsonProperty("verstrektDoorPartij")
    val verstrektDoorPartij: ObjectReference? = null,
    @JsonProperty("adres")
    val adres: String,
    @JsonProperty("soortDigitaalAdres")
    val soortDigitaalAdres: SoortDigitaalAdres,
    @JsonProperty("isStandaardAdres")
    val isStandaardAdres: Boolean? = null,
    @JsonProperty("omschrijving")
    val omschrijving: String? = null,
    @JsonProperty("referentie")
    val referentie: String? = null,
    @JsonProperty("verificatieDatum")
    val verificatieDatum: String? = null,
) : Referable
