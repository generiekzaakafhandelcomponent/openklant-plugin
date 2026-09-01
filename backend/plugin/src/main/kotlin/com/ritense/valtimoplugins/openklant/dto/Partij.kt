package com.ritense.valtimoplugins.openklant.dto

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonValue
import java.util.UUID

data class Partij(
    @JsonProperty("uuid")
    override val uuid: UUID,
    @JsonProperty("url")
    override val url: String,
    @JsonProperty("nummer")
    val nummer: String?,
    @JsonProperty("interneNotitie")
    val interneNotitie: String?,
    @JsonProperty("betrokkenen")
    val betrokkenen: List<ObjectReference>,
    @JsonProperty("categorieRelaties")
    val categorieRelaties: List<CategorieRelatiePartij>,
    @JsonProperty("digitaleAdressen")
    val digitaleAdressen: List<ObjectReference>?,
    @JsonProperty("voorkeursDigitaalAdres")
    val voorkeursDigitaalAdres: ObjectReference?,
    @JsonProperty("vertegenwoordigden")
    val vertegenwoordigden: List<ObjectReference>,
    @JsonProperty("rekeningnummers")
    val rekeningnummers: List<ObjectReference>?,
    @JsonProperty("voorkeursRekeningnummer")
    val voorkeursRekeningnummer: ObjectReference?,
    @JsonProperty("partijIdentificatoren")
    val partijIdentificatoren: List<PartijIdentificator>?,
    @JsonProperty("soortPartij")
    val soortPartij: SoortPartij,
    @JsonProperty("indicatieGeheimhouding")
    val indicatieGeheimhouding: Boolean?,
    @JsonProperty("voorkeurstaal")
    val voorkeurstaal: String?,
    @JsonProperty("indicatieActief")
    val indicatieActief: Boolean,
    @JsonProperty("bezoekadres")
    val bezoekadres: Adres?,
    @JsonProperty("correspondentieadres")
    val correspondentieadres: Adres?,
    @JsonProperty("partijIdentificatie")
    val partijIdentificatie: PartijIdentificatie?,
) : Referable {
    data class CategorieRelatiePartij(
        @JsonProperty("uuid")
        val uuid: String,
        @JsonProperty("url")
        val url: String,
        @JsonProperty("categorieNaam")
        val categorieNaam: String?,
        @JsonProperty("beginDatum")
        val beginDatum: String?,
        @JsonProperty("eindDatum")
        val eindDatum: String?,
    )

    enum class SoortPartij(
        val value: String,
    ) {
        CONTACTPERSOON("contactpersoon"),
        PERSOON("persoon"),
        ORGANISATIE("organisatie"),
        EXPAND_PARTIJ("ExpandPartij"),
        ;

        @JsonValue
        fun toJson() = value

        companion object {
            @JvmStatic
            @JsonCreator
            fun fromValue(value: String): SoortPartij =
                entries.firstOrNull { it.value.equals(value.trim(), ignoreCase = true) }
                    ?: throw IllegalArgumentException(
                        "Unknown soortPartij '$value'. Supported values: ${entries.joinToString { it.value }}",
                    )
        }
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    data class PartijIdentificatie(
        @JsonProperty("contactnaam")
        val contactnaam: Contactnaam?,
        @JsonProperty("volledigeNaam")
        val volledigeNaam: String? = null,
    )
}
