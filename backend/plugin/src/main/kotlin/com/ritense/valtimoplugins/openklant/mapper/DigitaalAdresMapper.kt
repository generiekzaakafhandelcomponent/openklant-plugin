package com.ritense.valtimoplugins.openklant.mapper

import com.ritense.valtimoplugins.openklant.dto.DigitaalAdresCreationRequest
import com.ritense.valtimoplugins.openklant.dto.DigitaalAdresPatchRequest
import com.ritense.valtimoplugins.openklant.dto.DigitaalAdresResponse
import com.ritense.valtimoplugins.openklant.dto.NestedUuid
import com.ritense.valtimoplugins.openklant.dto.ObjectReference
import com.ritense.valtimoplugins.openklant.model.DigitaalAdres
import com.ritense.valtimoplugins.openklant.model.DigitaalAdresPatch
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

fun DigitaalAdres.toCreationRequest(): DigitaalAdresCreationRequest =
    DigitaalAdresCreationRequest(
        verstrektDoorBetrokkene = verstrektDoorBetrokkeneUuid?. let { NestedUuid(verstrektDoorBetrokkeneUuid) },
        verstrektDoorPartij =
            NestedUuid(
                verstrektDoorPartijUuid ?: error("verstrektDoorPartij is required"),
            ),
        adres = adres,
        soortDigitaalAdres = soortDigitaalAdres,
        isStandaardAdres = isStandaardAdres,
        omschrijving = omschrijving ?: "",
        referentie = referentie,
        verificatieDatum = verificatieDatum,
    )

fun DigitaalAdres.toResponse(): DigitaalAdresResponse {
    val uuid = requireNotNull(uuid) { "uuid is required" }

    val betrokkeneUuid = requireNotNull(verstrektDoorBetrokkeneUuid)
    val betrokkeneUrl = requireNotNull(verstrektDoorBetrokkeneUrl)

    val partijUuid = requireNotNull(verstrektDoorPartijUuid)
    val partijUrl = requireNotNull(verstrektDoorPartijUrl)

    return DigitaalAdresResponse(
        uuid = uuid,
        url = url.toString(),
        verstrektDoorBetrokkene = ObjectReference(uuid = betrokkeneUuid, url = betrokkeneUrl.toString()),
        verstrektDoorPartij = ObjectReference(uuid = partijUuid, url = partijUrl.toString()),
        adres = adres,
        soortDigitaalAdres = soortDigitaalAdres,
        isStandaardAdres = isStandaardAdres,
        omschrijving = omschrijving,
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
