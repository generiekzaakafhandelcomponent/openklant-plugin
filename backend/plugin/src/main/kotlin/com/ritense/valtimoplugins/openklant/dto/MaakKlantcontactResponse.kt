package com.ritense.valtimoplugins.openklant.dto

import com.fasterxml.jackson.annotation.JsonProperty

/** Result of 'maak-klantcontact': the klantcontact, betrokkene and onderwerpobject created in one call. */
data class MaakKlantcontactResponse(
    @JsonProperty("klantcontact")
    val klantcontact: Klantcontact,
    @JsonProperty("betrokkene")
    val betrokkene: Betrokkene? = null,
    @JsonProperty("onderwerpobject")
    val onderwerpobject: Onderwerpobject? = null,
)
