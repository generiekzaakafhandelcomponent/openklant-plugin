package com.ritense.valtimoplugins.openklant.dto

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.ritense.valtimoplugins.openklant.model.NestedUuid

@JsonInclude(JsonInclude.Include.NON_NULL)
data class CreateBijlageRequest(
    @JsonProperty("wasBijlageVanKlantcontact")
    val wasBijlageVanKlantcontact: NestedUuid? = null,
    @JsonProperty("bijlageidentificator")
    val bijlageidentificator: BijlageIdentificator? = null,
)
