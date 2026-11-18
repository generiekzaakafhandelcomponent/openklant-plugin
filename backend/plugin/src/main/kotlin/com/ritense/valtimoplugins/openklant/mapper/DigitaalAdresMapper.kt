package com.ritense.valtimoplugins.openklant.mapper

import com.ritense.valtimoplugins.openklant.dto.DigitaalAdresCreationRequest
import com.ritense.valtimoplugins.openklant.dto.DigitaalAdresPatchRequest
import com.ritense.valtimoplugins.openklant.dto.DigitaalAdresResponse
import com.ritense.valtimoplugins.openklant.model.DigitaalAdres
import com.ritense.valtimoplugins.openklant.model.DigitaalAdresPatch
import com.ritense.valtimoplugins.openklant.model.NestedUuid
import java.net.URI

fun DigitaalAdresCreationRequest.toModel(): DigitaalAdres =
    DigitaalAdres(
        uuid = null,
        url = null,
        verstrektDoorBetrokkeneUuid = null,
        verstrektDoorBetrokkeneUrl = null,
        verstrektDoorPartijUuid = null,
        verstrektDoorPartijUrl = null,
        adres = adres,
        soortDigitaalAdres = soortDigitaalAdres,
        isStandaardAdres = isStandaardAdres,
        omschrijving = omschrijving,
        referentie = referentie,
        verificatieDatum = verificatieDatum,
    )

fun DigitaalAdresResponse.toModel(): DigitaalAdres =
    DigitaalAdres(
        uuid = uuid,
        url = URI(url),
        verstrektDoorBetrokkeneUuid = verstrektDoorBetrokkene?.uuid?.let { verstrektDoorBetrokkene.uuid },
        verstrektDoorBetrokkeneUrl = verstrektDoorBetrokkene?.url?.let { URI(verstrektDoorBetrokkene.url) },
        verstrektDoorPartijUuid = verstrektDoorPartij?.uuid,
        verstrektDoorPartijUrl = verstrektDoorPartij?.url?.let { URI(verstrektDoorPartij.url) },
        adres = adres,
        soortDigitaalAdres = soortDigitaalAdres,
        isStandaardAdres = isStandaardAdres,
        omschrijving = omschrijving,
        referentie = referentie,
        verificatieDatum = verificatieDatum,
    )

/**
 * An adres must be traceable to whoever provided it: a partij, a betrokkene or both. Open Klant only accepts
 * isStandaardAdres for an adres of a partij.
 */
fun DigitaalAdres.toCreationRequest(): DigitaalAdresCreationRequest {
    require(verstrektDoorPartijUuid != null || verstrektDoorBetrokkeneUuid != null) {
        "A digitaal adres needs verstrektDoorPartij, verstrektDoorBetrokkene or both"
    }
    require(isStandaardAdres != true || verstrektDoorPartijUuid != null) {
        "isStandaardAdres can only be set for a digitaal adres with verstrektDoorPartij"
    }
    return DigitaalAdresCreationRequest(
        verstrektDoorBetrokkene = verstrektDoorBetrokkeneUuid?.let { NestedUuid(it) },
        verstrektDoorPartij = verstrektDoorPartijUuid?.let { NestedUuid(it) },
        adres = adres,
        soortDigitaalAdres = soortDigitaalAdres,
        isStandaardAdres = isStandaardAdres,
        omschrijving = omschrijving ?: "",
        referentie = referentie,
        verificatieDatum = verificatieDatum,
    )
}

fun DigitaalAdresPatchRequest.toModel(): DigitaalAdresPatch =
    DigitaalAdresPatch(
        verstrektDoorBetrokkeneUuid =
            verstrektDoorBetrokkene?.uuid,
        verstrektDoorPartijUuid =
            verstrektDoorPartij?.uuid,
        adres = adres,
        soortDigitaalAdres = soortDigitaalAdres,
        isStandaardAdres = isStandaardAdres,
        omschrijving = omschrijving,
        referentie = referentie,
        verificatieDatum = verificatieDatum,
    )

fun DigitaalAdresPatch.toRequest(): DigitaalAdresPatchRequest =
    DigitaalAdresPatchRequest(
        verstrektDoorBetrokkene =
            verstrektDoorBetrokkeneUuid?.let { uuid ->
                NestedUuid(uuid)
            },
        verstrektDoorPartij =
            verstrektDoorPartijUuid?.let { uuid ->
                NestedUuid(uuid)
            },
        adres = adres,
        soortDigitaalAdres = soortDigitaalAdres,
        isStandaardAdres = isStandaardAdres,
        omschrijving = omschrijving,
        referentie = referentie,
        verificatieDatum = verificatieDatum,
    )
