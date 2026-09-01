package com.ritense.valtimoplugins.openklant.client

import com.ritense.valtimoplugins.openklant.dto.Actor
import com.ritense.valtimoplugins.openklant.dto.ActorKlantcontact
import com.ritense.valtimoplugins.openklant.dto.Betrokkene
import com.ritense.valtimoplugins.openklant.dto.Bijlage
import com.ritense.valtimoplugins.openklant.dto.CreateActorKlantcontactRequest
import com.ritense.valtimoplugins.openklant.dto.CreateActorRequest
import com.ritense.valtimoplugins.openklant.dto.CreateBetrokkeneRequest
import com.ritense.valtimoplugins.openklant.dto.CreateBijlageRequest
import com.ritense.valtimoplugins.openklant.dto.CreateInterneTaakRequest
import com.ritense.valtimoplugins.openklant.dto.CreateOnderwerpobjectRequest
import com.ritense.valtimoplugins.openklant.dto.CreatePartijIdentificatorRequest
import com.ritense.valtimoplugins.openklant.dto.CreatePartijRequest
import com.ritense.valtimoplugins.openklant.dto.CreateRekeningnummerRequest
import com.ritense.valtimoplugins.openklant.dto.CreateVertegenwoordigingRequest
import com.ritense.valtimoplugins.openklant.dto.DigitaalAdresCreationRequest
import com.ritense.valtimoplugins.openklant.dto.DigitaalAdresPatchRequest
import com.ritense.valtimoplugins.openklant.dto.DigitaalAdresResponse
import com.ritense.valtimoplugins.openklant.dto.InterneTaak
import com.ritense.valtimoplugins.openklant.dto.Klantcontact
import com.ritense.valtimoplugins.openklant.dto.KlantcontactCreationRequest
import com.ritense.valtimoplugins.openklant.dto.MaakKlantcontactResponse
import com.ritense.valtimoplugins.openklant.dto.Onderwerpobject
import com.ritense.valtimoplugins.openklant.dto.Partij
import com.ritense.valtimoplugins.openklant.dto.PartijIdentificator
import com.ritense.valtimoplugins.openklant.dto.PatchActorKlantcontactRequest
import com.ritense.valtimoplugins.openklant.dto.PatchActorRequest
import com.ritense.valtimoplugins.openklant.dto.PatchBetrokkeneRequest
import com.ritense.valtimoplugins.openklant.dto.PatchBijlageRequest
import com.ritense.valtimoplugins.openklant.dto.PatchInterneTaakRequest
import com.ritense.valtimoplugins.openklant.dto.PatchKlantcontactRequest
import com.ritense.valtimoplugins.openklant.dto.PatchOnderwerpobjectRequest
import com.ritense.valtimoplugins.openklant.dto.PatchPartijIdentificatorRequest
import com.ritense.valtimoplugins.openklant.dto.PatchPartijRequest
import com.ritense.valtimoplugins.openklant.dto.PatchRekeningnummerRequest
import com.ritense.valtimoplugins.openklant.dto.PatchVertegenwoordigingRequest
import com.ritense.valtimoplugins.openklant.dto.Rekeningnummer
import com.ritense.valtimoplugins.openklant.dto.Vertegenwoordiging
import com.ritense.valtimoplugins.openklant.model.DigitaalAdresQuery
import com.ritense.valtimoplugins.openklant.model.KlantcontactQuery
import com.ritense.valtimoplugins.openklant.model.KlantcontactQueryParamNames
import com.ritense.valtimoplugins.openklant.model.NestedUuid
import com.ritense.valtimoplugins.openklant.model.OpenKlantProperties
import com.ritense.valtimoplugins.openklant.model.OpenKlantQuery
import com.ritense.zgw.Page
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.validation.Valid
import org.jetbrains.annotations.VisibleForTesting
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.client.HttpServerErrorException
import org.springframework.web.client.RestClient
import org.springframework.web.client.RestClientResponseException
import org.springframework.web.client.body
import org.springframework.web.server.ResponseStatusException
import org.springframework.web.util.UriBuilder
import java.net.URI
import java.util.UUID

@Suppress("TooManyFunctions")
class OpenKlantClient(
    private val openKlantRestClientBuilder: RestClient.Builder,
) {
    // Partijen

    fun getPartijByBsn(
        bsn: String,
        properties: OpenKlantProperties,
    ): Partij? =
        execute("fetching Partij") {
            restClient(properties = properties)
                .get()
                .uri { uriBuilder ->
                    uriBuilder
                        .path(OK_PARTIJEN_PATH)
                        .queryParam(KlantcontactQueryParamNames.PARTIJIDENTIFICATOR__CODESOORTOBJECTID.value, "bsn")
                        .queryParam(KlantcontactQueryParamNames.PARTIJIDENTIFICATOR__OBJECTID.value, bsn)
                        .queryParam(KlantcontactQueryParamNames.SOORTPARTIJ.value, "persoon")
                        .build()
                }.accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body<Page<Partij>>()
                ?.results
                ?.firstOrNull()
        }

    fun getPartijen(
        query: OpenKlantQuery,
        properties: OpenKlantProperties,
    ): List<Partij> = listAll(OK_PARTIJEN_PATH, query, properties)

    fun getPartij(
        uuid: UUID,
        properties: OpenKlantProperties,
    ): Partij = retrieve(OK_PARTIJEN_PATH, uuid, properties)

    fun createPartij(
        request: CreatePartijRequest,
        properties: OpenKlantProperties,
    ): Partij = create(OK_PARTIJEN_PATH, request, properties)

    fun patchPartij(
        uuid: UUID,
        request: PatchPartijRequest,
        properties: OpenKlantProperties,
    ): Partij = patch(OK_PARTIJEN_PATH, uuid, request, properties)

    fun patchPartij(
        id: String,
        patchData: Map<String, Any>,
        properties: OpenKlantProperties,
    ): Partij = patch(OK_PARTIJEN_PATH, UUID.fromString(id), patchData, properties)

    fun deletePartij(
        uuid: UUID,
        properties: OpenKlantProperties,
    ) = delete(OK_PARTIJEN_PATH, uuid, properties)

    // Digitale adressen

    fun getDigitaleAdressen(
        query: DigitaalAdresQuery,
        properties: OpenKlantProperties,
    ): List<DigitaalAdresResponse> =
        execute("fetching adressen") {
            restClient(properties = properties)
                .get()
                .uri { uriBuilder -> buildDigitaalAdresUri(uriBuilder, query) }
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body<Page<DigitaalAdresResponse>>()
                ?.results
                ?: throw IllegalStateException("Error fetching DigitaalAdres(sen): response body was null")
        }

    fun createDigitaalAdres(
        request: DigitaalAdresCreationRequest,
        properties: OpenKlantProperties,
    ): DigitaalAdresResponse = create(OK_DIGITALE_ADRESSEN_PATH, request, properties)

    fun getDigitaalAdres(
        digitaalAdresUuid: NestedUuid,
        properties: OpenKlantProperties,
    ): DigitaalAdresResponse = retrieve(OK_DIGITALE_ADRESSEN_PATH, digitaalAdresUuid.uuid, properties)

    fun updateDigitaalAdres(
        digitaalAdresUuid: NestedUuid,
        patchData: DigitaalAdresPatchRequest,
        properties: OpenKlantProperties,
    ): DigitaalAdresResponse = patch(OK_DIGITALE_ADRESSEN_PATH, digitaalAdresUuid.uuid, patchData, properties)

    fun deleteDigitaalAdres(
        uuid: UUID,
        properties: OpenKlantProperties,
    ) = delete(OK_DIGITALE_ADRESSEN_PATH, uuid, properties)

    // Klantcontacten

    fun getKlantcontacten(
        query: KlantcontactQuery,
        properties: OpenKlantProperties,
    ): Page<Klantcontact> {
        if (query.bsn.isNullOrBlank() &&
            query.objectUuid.isNullOrBlank() &&
            query.partijUuid.isNullOrBlank()
        ) {
            return Page(count = 0, results = emptyList())
        }

        return execute("fetching Klantcontacten") {
            restClient(properties = properties)
                .get()
                .uri { uriBuilder -> buildKlantcontactUri(builder = uriBuilder, query = query) }
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body<Page<Klantcontact>>()
                ?: throw IllegalStateException("Error fetching Klantcontacten: response body was null")
        }
    }

    fun postKlantcontact(
        @Valid @RequestBody request: KlantcontactCreationRequest,
        properties: OpenKlantProperties,
    ) {
        execute("creating Klantcontact") {
            restClient(properties = properties)
                .post()
                .uri(OK_MAAK_KLANTCONTACT_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .toBodilessEntity()
        }
    }

    /** Creates a klantcontact together with its betrokkene and onderwerpobject in a single call, and returns all three. */
    fun maakKlantcontact(
        request: KlantcontactCreationRequest,
        properties: OpenKlantProperties,
    ): MaakKlantcontactResponse = create(OK_MAAK_KLANTCONTACT_PATH, request, properties)

    fun searchKlantcontacten(
        query: OpenKlantQuery,
        properties: OpenKlantProperties,
    ): List<Klantcontact> = listAll(OK_KLANTCONTACTEN_PATH, query, properties)

    fun getKlantcontact(
        uuid: UUID,
        properties: OpenKlantProperties,
    ): Klantcontact = retrieve(OK_KLANTCONTACTEN_PATH, uuid, properties)

    fun patchKlantcontact(
        uuid: UUID,
        request: PatchKlantcontactRequest,
        properties: OpenKlantProperties,
    ): Klantcontact = patch(OK_KLANTCONTACTEN_PATH, uuid, request, properties)

    fun deleteKlantcontact(
        uuid: UUID,
        properties: OpenKlantProperties,
    ) = delete(OK_KLANTCONTACTEN_PATH, uuid, properties)

    // Actoren

    fun getActoren(
        query: OpenKlantQuery,
        properties: OpenKlantProperties,
    ): List<Actor> = listAll(OK_ACTOREN_PATH, query, properties)

    fun getActor(
        uuid: UUID,
        properties: OpenKlantProperties,
    ): Actor = retrieve(OK_ACTOREN_PATH, uuid, properties)

    fun createActor(
        request: CreateActorRequest,
        properties: OpenKlantProperties,
    ): Actor = create(OK_ACTOREN_PATH, request, properties)

    fun patchActor(
        uuid: UUID,
        request: PatchActorRequest,
        properties: OpenKlantProperties,
    ): Actor = patch(OK_ACTOREN_PATH, uuid, request, properties)

    fun deleteActor(
        uuid: UUID,
        properties: OpenKlantProperties,
    ) = delete(OK_ACTOREN_PATH, uuid, properties)

    // Actorklantcontacten

    fun getActorKlantcontacten(
        query: OpenKlantQuery,
        properties: OpenKlantProperties,
    ): List<ActorKlantcontact> = listAll(OK_ACTORKLANTCONTACTEN_PATH, query, properties)

    fun getActorKlantcontact(
        uuid: UUID,
        properties: OpenKlantProperties,
    ): ActorKlantcontact = retrieve(OK_ACTORKLANTCONTACTEN_PATH, uuid, properties)

    fun createActorKlantcontact(
        request: CreateActorKlantcontactRequest,
        properties: OpenKlantProperties,
    ): ActorKlantcontact = create(OK_ACTORKLANTCONTACTEN_PATH, request, properties)

    fun patchActorKlantcontact(
        uuid: UUID,
        request: PatchActorKlantcontactRequest,
        properties: OpenKlantProperties,
    ): ActorKlantcontact = patch(OK_ACTORKLANTCONTACTEN_PATH, uuid, request, properties)

    fun deleteActorKlantcontact(
        uuid: UUID,
        properties: OpenKlantProperties,
    ) = delete(OK_ACTORKLANTCONTACTEN_PATH, uuid, properties)

    // Betrokkenen

    fun getBetrokkenen(
        query: OpenKlantQuery,
        properties: OpenKlantProperties,
    ): List<Betrokkene> = listAll(OK_BETROKKENEN_PATH, query, properties)

    fun getBetrokkene(
        uuid: UUID,
        properties: OpenKlantProperties,
    ): Betrokkene = retrieve(OK_BETROKKENEN_PATH, uuid, properties)

    fun createBetrokkene(
        request: CreateBetrokkeneRequest,
        properties: OpenKlantProperties,
    ): Betrokkene = create(OK_BETROKKENEN_PATH, request, properties)

    fun patchBetrokkene(
        uuid: UUID,
        request: PatchBetrokkeneRequest,
        properties: OpenKlantProperties,
    ): Betrokkene = patch(OK_BETROKKENEN_PATH, uuid, request, properties)

    fun deleteBetrokkene(
        uuid: UUID,
        properties: OpenKlantProperties,
    ) = delete(OK_BETROKKENEN_PATH, uuid, properties)

    // Bijlagen

    fun getBijlagen(
        query: OpenKlantQuery,
        properties: OpenKlantProperties,
    ): List<Bijlage> = listAll(OK_BIJLAGEN_PATH, query, properties)

    fun getBijlage(
        uuid: UUID,
        properties: OpenKlantProperties,
    ): Bijlage = retrieve(OK_BIJLAGEN_PATH, uuid, properties)

    fun createBijlage(
        request: CreateBijlageRequest,
        properties: OpenKlantProperties,
    ): Bijlage = create(OK_BIJLAGEN_PATH, request, properties)

    fun patchBijlage(
        uuid: UUID,
        request: PatchBijlageRequest,
        properties: OpenKlantProperties,
    ): Bijlage = patch(OK_BIJLAGEN_PATH, uuid, request, properties)

    fun deleteBijlage(
        uuid: UUID,
        properties: OpenKlantProperties,
    ) = delete(OK_BIJLAGEN_PATH, uuid, properties)

    // Interne taken

    fun getInterneTaken(
        query: OpenKlantQuery,
        properties: OpenKlantProperties,
    ): List<InterneTaak> = listAll(OK_INTERNETAKEN_PATH, query, properties)

    fun getInterneTaak(
        uuid: UUID,
        properties: OpenKlantProperties,
    ): InterneTaak = retrieve(OK_INTERNETAKEN_PATH, uuid, properties)

    fun createInterneTaak(
        request: CreateInterneTaakRequest,
        properties: OpenKlantProperties,
    ): InterneTaak = create(OK_INTERNETAKEN_PATH, request, properties)

    fun patchInterneTaak(
        uuid: UUID,
        request: PatchInterneTaakRequest,
        properties: OpenKlantProperties,
    ): InterneTaak = patch(OK_INTERNETAKEN_PATH, uuid, request, properties)

    fun deleteInterneTaak(
        uuid: UUID,
        properties: OpenKlantProperties,
    ) = delete(OK_INTERNETAKEN_PATH, uuid, properties)

    // Onderwerpobjecten

    fun getOnderwerpobjecten(
        query: OpenKlantQuery,
        properties: OpenKlantProperties,
    ): List<Onderwerpobject> = listAll(OK_ONDERWERPOBJECTEN_PATH, query, properties)

    fun getOnderwerpobject(
        uuid: UUID,
        properties: OpenKlantProperties,
    ): Onderwerpobject = retrieve(OK_ONDERWERPOBJECTEN_PATH, uuid, properties)

    fun createOnderwerpobject(
        request: CreateOnderwerpobjectRequest,
        properties: OpenKlantProperties,
    ): Onderwerpobject = create(OK_ONDERWERPOBJECTEN_PATH, request, properties)

    fun patchOnderwerpobject(
        uuid: UUID,
        request: PatchOnderwerpobjectRequest,
        properties: OpenKlantProperties,
    ): Onderwerpobject = patch(OK_ONDERWERPOBJECTEN_PATH, uuid, request, properties)

    fun deleteOnderwerpobject(
        uuid: UUID,
        properties: OpenKlantProperties,
    ) = delete(OK_ONDERWERPOBJECTEN_PATH, uuid, properties)

    // Partij-identificatoren

    fun getPartijIdentificatoren(
        query: OpenKlantQuery,
        properties: OpenKlantProperties,
    ): List<PartijIdentificator> = listAll(OK_PARTIJ_IDENTIFICATOREN_PATH, query, properties)

    fun getPartijIdentificator(
        uuid: UUID,
        properties: OpenKlantProperties,
    ): PartijIdentificator = retrieve(OK_PARTIJ_IDENTIFICATOREN_PATH, uuid, properties)

    fun createPartijIdentificator(
        request: CreatePartijIdentificatorRequest,
        properties: OpenKlantProperties,
    ): PartijIdentificator = create(OK_PARTIJ_IDENTIFICATOREN_PATH, request, properties)

    fun patchPartijIdentificator(
        uuid: UUID,
        request: PatchPartijIdentificatorRequest,
        properties: OpenKlantProperties,
    ): PartijIdentificator = patch(OK_PARTIJ_IDENTIFICATOREN_PATH, uuid, request, properties)

    fun deletePartijIdentificator(
        uuid: UUID,
        properties: OpenKlantProperties,
    ) = delete(OK_PARTIJ_IDENTIFICATOREN_PATH, uuid, properties)

    // Rekeningnummers

    fun getRekeningnummers(
        query: OpenKlantQuery,
        properties: OpenKlantProperties,
    ): List<Rekeningnummer> = listAll(OK_REKENINGNUMMERS_PATH, query, properties)

    fun getRekeningnummer(
        uuid: UUID,
        properties: OpenKlantProperties,
    ): Rekeningnummer = retrieve(OK_REKENINGNUMMERS_PATH, uuid, properties)

    fun createRekeningnummer(
        request: CreateRekeningnummerRequest,
        properties: OpenKlantProperties,
    ): Rekeningnummer = create(OK_REKENINGNUMMERS_PATH, request, properties)

    fun patchRekeningnummer(
        uuid: UUID,
        request: PatchRekeningnummerRequest,
        properties: OpenKlantProperties,
    ): Rekeningnummer = patch(OK_REKENINGNUMMERS_PATH, uuid, request, properties)

    fun deleteRekeningnummer(
        uuid: UUID,
        properties: OpenKlantProperties,
    ) = delete(OK_REKENINGNUMMERS_PATH, uuid, properties)

    // Vertegenwoordigingen

    fun getVertegenwoordigingen(
        query: OpenKlantQuery,
        properties: OpenKlantProperties,
    ): List<Vertegenwoordiging> = listAll(OK_VERTEGENWOORDIGINGEN_PATH, query, properties)

    fun getVertegenwoordiging(
        uuid: UUID,
        properties: OpenKlantProperties,
    ): Vertegenwoordiging = retrieve(OK_VERTEGENWOORDIGINGEN_PATH, uuid, properties)

    fun createVertegenwoordiging(
        request: CreateVertegenwoordigingRequest,
        properties: OpenKlantProperties,
    ): Vertegenwoordiging = create(OK_VERTEGENWOORDIGINGEN_PATH, request, properties)

    fun patchVertegenwoordiging(
        uuid: UUID,
        request: PatchVertegenwoordigingRequest,
        properties: OpenKlantProperties,
    ): Vertegenwoordiging = patch(OK_VERTEGENWOORDIGINGEN_PATH, uuid, request, properties)

    fun deleteVertegenwoordiging(
        uuid: UUID,
        properties: OpenKlantProperties,
    ) = delete(OK_VERTEGENWOORDIGINGEN_PATH, uuid, properties)

    // Generic operations

    /** Fetches a single page of [path]. */
    private inline fun <reified T : Any> listPage(
        path: String,
        query: OpenKlantQuery,
        properties: OpenKlantProperties,
    ): Page<T> =
        execute("fetching $path") {
            restClient(properties = properties)
                .get()
                .uri { uriBuilder -> buildCollectionUri(uriBuilder, path, query) }
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body<Page<T>>()
                ?: throw IllegalStateException("Error fetching $path: response body was null")
        }

    /** Fetches every page of [path], unless the caller pinned a specific page through the 'page' parameter. */
    private inline fun <reified T : Any> listAll(
        path: String,
        query: OpenKlantQuery,
        properties: OpenKlantProperties,
    ): List<T> =
        if (query.queryParams.containsKey(PAGE_PARAM)) {
            listPage<T>(path, query, properties).results
        } else {
            Page.getAll { pageNumber ->
                val pagedQuery = OpenKlantQuery(query.queryParams.toMutableMap())
                pagedQuery.add(PAGE_PARAM, pageNumber.toString())
                listPage<T>(path, pagedQuery, properties)
            }
        }

    private inline fun <reified T : Any> retrieve(
        path: String,
        uuid: UUID,
        properties: OpenKlantProperties,
    ): T =
        execute("fetching $path with uuid: $uuid") {
            restClient(properties = properties)
                .get()
                .uri("$path/$uuid")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body<T>()
                ?: throw IllegalStateException("Error fetching $path with uuid $uuid: response body was null")
        }

    private inline fun <reified T : Any> create(
        path: String,
        request: Any,
        properties: OpenKlantProperties,
    ): T =
        execute("creating $path") {
            restClient(properties = properties)
                .post()
                .uri(path)
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body<T>()
                ?: throw IllegalStateException("Error creating $path: response body was null")
        }

    private inline fun <reified T : Any> patch(
        path: String,
        uuid: UUID,
        request: Any,
        properties: OpenKlantProperties,
    ): T =
        execute("patching $path with uuid: $uuid") {
            restClient(properties = properties)
                .patch()
                .uri("$path/$uuid")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body<T>()
                ?: throw IllegalStateException("Error patching $path with uuid $uuid: response body was null")
        }

    private fun delete(
        path: String,
        uuid: UUID,
        properties: OpenKlantProperties,
    ) {
        execute("deleting $path with uuid: $uuid") {
            restClient(properties = properties)
                .delete()
                .uri("$path/$uuid")
                .retrieve()
                .toBodilessEntity()
        }
    }

    /** Runs [block], translating the Open Klant error responses into a [ResponseStatusException]. */
    private inline fun <T> execute(
        description: String,
        block: () -> T,
    ): T =
        try {
            block()
        } catch (e: HttpServerErrorException.InternalServerError) {
            handleInternalServerError(e)
        } catch (e: RestClientResponseException) {
            handleResponseException(e, "Error $description")
        }

    private fun restClient(properties: OpenKlantProperties): RestClient =
        openKlantRestClientBuilder
            .clone()
            .baseUrl(properties.klantinteractiesUrl.toASCIIString())
            .defaultHeader("Authorization", "Token ${properties.token}")
            .build()

    @VisibleForTesting
    internal fun buildKlantcontactUri(
        builder: UriBuilder,
        query: KlantcontactQuery,
    ): URI {
        query.objectTypeId?.let {
            builder.queryParam(
                KlantcontactQueryParamNames.ONDERWERPOBJECT__ONDERWERPOBJECTIDENTIFICATORCODEOBJECTTYPE.value,
                it,
            )
        }
        query.bsn?.let {
            builder.queryParam(
                KlantcontactQueryParamNames.HADBETROKKENE__WASPARTIJ__PARTIJIDENTIFICATOR__OBJECTID.value,
                it,
            )
        }
        query.objectUuid?.let {
            builder.queryParam(
                KlantcontactQueryParamNames.ONDERWERPOBJECT__ONDERWERPOBJECTIDENTIFICATOROBJECTID.value,
                it,
            )
        }
        query.partijUuid?.let {
            builder.queryParam(
                KlantcontactQueryParamNames.HADBETROKKENE__WASPARTIJ__UUID.value,
                it,
            )
        }
        return builder
            .path(OK_KLANTCONTACTEN_PATH)
            .build()
    }

    @VisibleForTesting
    internal fun buildDigitaalAdresUri(
        builder: UriBuilder,
        query: DigitaalAdresQuery,
    ): URI = buildCollectionUri(builder, OK_DIGITALE_ADRESSEN_PATH, OpenKlantQuery(query.queryParams))

    @VisibleForTesting
    internal fun buildCollectionUri(
        builder: UriBuilder,
        path: String,
        query: OpenKlantQuery,
    ): URI {
        query.queryParams.forEach { (key, value) ->
            builder.queryParam(key, value)
        }
        return builder
            .path(path)
            .build()
    }

    private fun handleInternalServerError(e: HttpServerErrorException.InternalServerError): Nothing {
        logger.warn { "Response body:  ${e.responseBodyAsString}" }
        logger.error(e) { "Internal Server Error calling Open Klant" }
        throw ResponseStatusException(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "Internal Server Error calling OpenKlant",
            e,
        )
    }

    private fun handleResponseException(
        e: RestClientResponseException,
        reason: String,
    ): Nothing {
        logger.warn(e) { "Client error calling Open Klant" }
        logger.warn { "Response body:  ${e.responseBodyAsString}" }
        throw ResponseStatusException(
            e.statusCode,
            reason,
            e,
        )
    }

    companion object {
        const val OK_ACTOREN_PATH = "actoren"
        const val OK_ACTORKLANTCONTACTEN_PATH = "actorklantcontacten"
        const val OK_BETROKKENEN_PATH = "betrokkenen"
        const val OK_BIJLAGEN_PATH = "bijlagen"
        const val OK_DIGITALE_ADRESSEN_PATH = "digitaleadressen"
        const val OK_INTERNETAKEN_PATH = "internetaken"
        const val OK_KLANTCONTACTEN_PATH = "klantcontacten"
        const val OK_MAAK_KLANTCONTACT_PATH = "maak-klantcontact"
        const val OK_ONDERWERPOBJECTEN_PATH = "onderwerpobjecten"
        const val OK_PARTIJEN_PATH = "partijen"
        const val OK_PARTIJ_IDENTIFICATOREN_PATH = "partij-identificatoren"
        const val OK_REKENINGNUMMERS_PATH = "rekeningnummers"
        const val OK_VERTEGENWOORDIGINGEN_PATH = "vertegenwoordigingen"

        const val PAGE_PARAM = "page"

        private val logger = KotlinLogging.logger { }
    }
}
