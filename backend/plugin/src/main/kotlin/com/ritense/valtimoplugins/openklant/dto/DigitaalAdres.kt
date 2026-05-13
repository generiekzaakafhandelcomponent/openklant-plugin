package com.ritense.valtimoplugins.openklant.dto

import com.fasterxml.jackson.annotation.JsonProperty
import java.util.UUID

data class DigitaalAdres(
    @JsonProperty("uuid")
    override val uuid: UUID,
    @JsonProperty("url")
    override val url: String,
    @JsonProperty("verstrektDoorBetrokkene")
    val verstrektDoorBetrokkene: ObjectReference?,
    @JsonProperty("verstrektDoorPartij")
    val verstrektDoorPartij: ObjectReference?,
    @JsonProperty("adres")
    val adres: String,
    @JsonProperty("soortDigitaalAdres")
    val soortDigitaalAdres: SoortDigitaalAdres,
    @JsonProperty("isStandaardAdres")
    val isStandaardAdres: Boolean?,
    @JsonProperty("omschrijving")
    val omschrijving: String?,
    @JsonProperty("referentie")
    val referentie: String?,
    @JsonProperty("_expand")
    val expand: Any?,
) : Referable
