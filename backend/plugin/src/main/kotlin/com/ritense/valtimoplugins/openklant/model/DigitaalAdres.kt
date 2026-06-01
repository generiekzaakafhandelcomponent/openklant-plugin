package com.ritense.valtimoplugins.openklant.model

import java.net.URI
import java.util.UUID

data class DigitaalAdres(
    val uuid: UUID? = null,
    val url: URI? = null,
    val verstrektDoorBetrokkeneUuid: UUID? = null,
    val verstrektDoorBetrokkeneUrl: URI? = null,
    val verstrektDoorPartijUuid: UUID? = null,
    val verstrektDoorPartijUrl: URI? = null,
    val adres: String,
    val soortDigitaalAdres: SoortDigitaalAdres,
    val isStandaardAdres: Boolean? = null,
    val omschrijving: String? = null,
    val referentie: String? = null,
    val verificatieDatum: String? = null,
)
