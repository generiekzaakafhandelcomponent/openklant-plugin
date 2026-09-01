package com.ritense.valtimoplugins.openklant.dto

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.ritense.valtimoplugins.openklant.model.NestedUuid

@JsonInclude(JsonInclude.Include.NON_NULL)
data class PatchPartijRequest(
    @JsonProperty("nummer")
    val nummer: String? = null,
    @JsonProperty("interneNotitie")
    val interneNotitie: String? = null,
    @JsonProperty("digitaleAdressen")
    val digitaleAdressen: List<NestedUuid>? = null,
    @JsonProperty("voorkeursDigitaalAdres")
    val voorkeursDigitaalAdres: NestedUuid? = null,
    @JsonProperty("rekeningnummers")
    val rekeningnummers: List<NestedUuid>? = null,
    @JsonProperty("voorkeursRekeningnummer")
    val voorkeursRekeningnummer: NestedUuid? = null,
    @JsonProperty("soortPartij")
    val soortPartij: Partij.SoortPartij? = null,
    @JsonProperty("indicatieGeheimhouding")
    val indicatieGeheimhouding: Boolean? = null,
    @JsonProperty("voorkeurstaal")
    val voorkeurstaal: String? = null,
    @JsonProperty("indicatieActief")
    val indicatieActief: Boolean? = null,
    @JsonProperty("bezoekadres")
    val bezoekadres: Adres? = null,
    @JsonProperty("correspondentieadres")
    val correspondentieadres: Adres? = null,
    @JsonProperty("partijIdentificatie")
    val partijIdentificatie: Partij.PartijIdentificatie? = null,
)
