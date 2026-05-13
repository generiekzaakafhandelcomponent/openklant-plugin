package com.ritense.valtimoplugins.openklant.dto

data class DigitaalAdresCreationResponse(
    val uuid: String,
    val url: String,
    val verstrektDoorBetrokkene: VerstrektDoor,
    val verstrektDoorPartij: VerstrektDoor,
    val adres: String,
    val soortDigitaalAdres: String,
    val isStandaardAdres: Boolean,
    val omschrijving: String,
    val referentie: String,
    val verificatieDatum: String
)