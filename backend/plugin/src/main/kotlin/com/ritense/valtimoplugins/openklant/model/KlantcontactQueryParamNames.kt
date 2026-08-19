package com.ritense.valtimoplugins.openklant.model

enum class KlantcontactQueryParamNames(
    val value: String,
) {
    PARTIJIDENTIFICATOR__CODESOORTOBJECTID(
        "partijIdentificator__codeSoortObjectId",
    ),
    PARTIJIDENTIFICATOR__OBJECTID(
        "partijIdentificator__objectId",
    ),
    SOORTPARTIJ(
        "soortPartij",
    ),
    ONDERWERPOBJECT__ONDERWERPOBJECTIDENTIFICATORCODEOBJECTTYPE(
        "onderwerpobject__onderwerpobjectidentificatorCodeObjecttype",
    ),
    ONDERWERPOBJECT__ONDERWERPOBJECTIDENTIFICATOROBJECTID(
        "onderwerpobject__onderwerpobjectidentificatorObjectId",
    ),
    HADBETROKKENE__WASPARTIJ__PARTIJIDENTIFICATOR__OBJECTID(
        "hadBetrokkene__wasPartij__partijIdentificator__objectId", // BSN
    ),
    HADBETROKKENE__WASPARTIJ__UUID(
        "hadBetrokkene__wasPartij__uuid",
    ),
}
