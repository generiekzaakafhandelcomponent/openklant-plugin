package com.ritense.valtimoplugins.openklant.dto

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.ritense.valtimoplugins.openklant.model.NestedUuid

@JsonInclude(JsonInclude.Include.NON_NULL)
data class PatchBetrokkeneRequest(
    @JsonProperty("hadKlantcontact")
    val hadKlantcontact: NestedUuid? = null,
    @JsonProperty("rol")
    val rol: Betrokkene.Rol? = null,
    @JsonProperty("initiator")
    val initiator: Boolean? = null,
    @JsonProperty("wasPartij")
    val wasPartij: NestedUuid? = null,
    @JsonProperty("bezoekadres")
    val bezoekadres: Adres? = null,
    @JsonProperty("correspondentieadres")
    val correspondentieadres: Adres? = null,
    @JsonProperty("contactnaam")
    val contactnaam: Contactnaam? = null,
    @JsonProperty("organisatienaam")
    val organisatienaam: String? = null,
)
