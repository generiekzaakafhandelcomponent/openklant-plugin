package com.ritense.valtimoplugins.openklant.dto

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.ritense.valtimoplugins.openklant.model.NestedUuid

@JsonInclude(JsonInclude.Include.NON_NULL)
data class CreatePartijRequest(
    @JsonProperty("nummer")
    val nummer: String?,
    @JsonProperty("interneNotitie")
    val interneNotitie: String,
    @JsonProperty("digitaleAdressen")
    val digitaleAdressen: List<NestedUuid>?,
    @JsonProperty("voorkeursDigitaalAdres")
    val voorkeursDigitaalAdres: NestedUuid?,
    @JsonProperty("rekeningnummers")
    val rekeningnummers: List<NestedUuid>?,
    @JsonProperty("voorkeursRekeningnummer")
    val voorkeursRekeningnummer: NestedUuid?,
    @JsonProperty("partijIdentificatoren")
    val partijIdentificatoren: List<Map<String, Identificator>>,
    @JsonProperty("soortPartij")
    val soortPartij: Partij.SoortPartij,
    @JsonProperty("indicatieGeheimhouding")
    val indicatieGeheimhouding: Boolean?,
    @JsonProperty("voorkeurstaal")
    val voorkeurstaal: String,
    @JsonProperty("indicatieActief")
    val indicatieActief: Boolean,
    @JsonProperty("bezoekadres")
    val bezoekadres: Adres?,
    @JsonProperty("correspondentieAdres")
    val correspondentieAdres: Adres?,
    @JsonProperty("partijIdentificatie")
    val partijIdentificatie: Partij.PartijIdentificatie?,
)
