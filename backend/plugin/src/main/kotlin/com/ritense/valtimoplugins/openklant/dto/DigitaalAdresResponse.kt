package com.ritense.valtimoplugins.openklant.dto

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size
import java.util.UUID

data class DigitaalAdresCreationResponse(
    @JsonProperty("uuid")
    @field:NotNull
    override val uuid: UUID,
    @JsonProperty("url")
    @field:NotNull
    @field:Size(min = 1, max = 1000)
    override val url: String,
    @JsonProperty("verstrektDoorBetrokkene")
    val verstrektDoorBetrokkene: ObjectReference? = null,
    @JsonProperty("verstrektDoorPartij")
    val verstrektDoorPartij: ObjectReference? = null,
    @JsonProperty("adres")
    @field:NotNull
    @field:Size(max = 80)
    val adres: String,
    @JsonProperty("soortDigitaalAdres")
    @field:NotNull
    @field:Pattern(
        regexp = "email|telefoonnummer|overig",
        message = "Does not match either 'email', 'telefoonnummer', or 'overig'",
    )
    val soortDigitaalAdres: SoortDigitaalAdres,
    @JsonProperty("isStandaardAdres")
    val isStandaardAdres: Boolean? = null,
    @JsonProperty("omschrijving")
    @field:Size(max = 40)
    val omschrijving: String? = null,
    @JsonProperty("referentie")
    @field:Size(max = 50)
    @field:Pattern(regexp = "^[-a-zA-Z0-9_]+$")
    val referentie: String? = null,
    @JsonProperty("verificatieDatum")
    val verificatieDatum: String? = null,
) : Referable
