package com.ritense.valtimoplugins.openklant.model

import com.ritense.valtimoplugins.openklant.dto.SoortDigitaalAdres
import java.util.UUID

data class DigitaalAdresPatch(
    val verstrektDoorBetrokkeneUuid: UUID? = null,
    val verstrektDoorPartijUuid: UUID? = null,
    val adres: String? = null,
    val soortDigitaalAdres: SoortDigitaalAdres? = null,
    val isStandaardAdres: Boolean? = null,
    val omschrijving: String? = null,
    val referentie: String? = null,
    val verificatieDatum: String? = null,
)
