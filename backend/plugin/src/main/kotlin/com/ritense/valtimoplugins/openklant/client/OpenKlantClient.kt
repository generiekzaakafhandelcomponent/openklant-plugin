package com.ritense.valtimoplugins.openklant.client

import com.ritense.valtimoplugins.openklant.dto.DigitaalAdresCreationRequest
import com.ritense.valtimoplugins.openklant.dto.CreatePartijRequest
import com.ritense.valtimoplugins.openklant.dto.DigitaalAdres
import com.ritense.valtimoplugins.openklant.dto.DigitaalAdresPatchRequest
import com.ritense.valtimoplugins.openklant.dto.Klantcontact
import com.ritense.valtimoplugins.openklant.dto.KlantcontactCreationRequest
import com.ritense.valtimoplugins.openklant.dto.Partij
import com.ritense.valtimoplugins.openklant.dto.UuidReference
import com.ritense.valtimoplugins.openklant.model.DigitaalAdresQuery
import com.ritense.valtimoplugins.openklant.model.KlantcontactQuery
import com.ritense.valtimoplugins.openklant.model.KlantcontactQueryParamNames
import com.ritense.valtimoplugins.openklant.model.OpenKlantProperties
import com.ritense.zgw.Page
import jakarta.validation.Valid
import mu.KotlinLogging
import org.jetbrains.annotations.VisibleForTesting
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.client.RestClient
import org.springframework.web.client.RestClientResponseException
import org.springframework.web.client.HttpServerErrorException
import org.springframework.web.client.body
import org.springframework.web.server.ResponseStatusException
import org.springframework.web.util.UriBuilder
import java.net.URI

class OpenKlantClient(
    private val openKlantRestClientBuilder: RestClient.Builder,
) {
    fun getPartijByBsn(
        bsn: String,
        properties: OpenKlantProperties,
    ): Partij? =
        try {
            restClient(properties = properties)
                .get()
                .uri { uriBuilder ->
                    uriBuilder
                        .path(OK_PARTIJEN_PATH)
                        .queryParam(KlantcontactQueryParamNames.PARTIJIDENTIFICATOR__CODESOORTOBJECTID.value, "bsn")
                        .queryParam(KlantcontactQueryParamNames.PARTIJIDENTIFICATOR__OBJECTID.value, bsn)
                        .queryParam(KlantcontactQueryParamNames.SOORTPARTIJ.value, "persoon")
                        .build()
                }.retrieve()
                .body<Page<Partij>>()
                ?.results
                ?.firstOrNull()
        } catch (e: HttpServerErrorException.InternalServerError) {
            handleInternalServerError(e)
        } catch (e: RestClientResponseException) {
            handleResponseException(e, "Error fetching Partij")
        }

    fun createPartij(
        request: CreatePartijRequest,
        properties: OpenKlantProperties,
    ): Partij =
        try {
            restClient(properties = properties)
                .post()
                .uri(OK_PARTIJEN_PATH)
                .body(request)
                .retrieve()
                .body<Partij>() ?: throw IllegalStateException("Error creating Partij: response body was null")
        } catch (e: HttpServerErrorException.InternalServerError) {
            handleInternalServerError(e)
        } catch (e: RestClientResponseException) {
            handleResponseException(e, "Error creating Partij")
        }

    fun patchPartij(
        id: String,
        patchData: Map<String, Any>,
        properties: OpenKlantProperties,
    ): Partij =
        try {
            restClient(properties = properties)
                .patch()
                .uri("$OK_PARTIJEN_PATH/$id")
                .body(patchData)
                .retrieve()
                .body<Partij>()
                ?: throw IllegalStateException("Error patching Partij: response body was null")
        } catch (e: HttpServerErrorException.InternalServerError) {
            handleInternalServerError(e)
        } catch (e: RestClientResponseException) {
            handleResponseException(e, "Error patching Partij")
        }

    fun getDigitaleAdressen(
        query: DigitaalAdresQuery,
        properties: OpenKlantProperties
    ): List<DigitaalAdres> =
        try {
            restClient(properties = properties)
                .get()
                .uri { uriBuilder ->
                    buildDigitaalAdresUri(uriBuilder, query)
                }.retrieve()
                .body<Page<DigitaalAdres>>()
                ?.results
                ?: throw IllegalStateException("Error fetching DigitaalAdres(sen): response body was null")

        } catch (e: HttpServerErrorException.InternalServerError) {
            handleInternalServerError(e)
        } catch (e: RestClientResponseException) {
            handleResponseException(
                e,
                "Error fetching adressen"
            )
        }


    fun createDigitaalAdres(
        request: DigitaalAdresCreationRequest,
        properties: OpenKlantProperties,
    ): DigitaalAdres =
        try {
            restClient(properties = properties)
                .post()
                .uri(OK_DIGITALE_ADRESSEN_PATH)
                .body(request)
                .retrieve()
                .body<DigitaalAdres>()
                ?: throw IllegalStateException("Error creating DigitaalAdres: response body was null")
        } catch (e: HttpServerErrorException.InternalServerError) {
            handleInternalServerError(e)
        } catch (e: RestClientResponseException) {
            handleResponseException(e, "Error creating DigitaalAdres")
        }

    fun getDigitaalAdres(
        digitaalAdresUuid: UuidReference,
        properties: OpenKlantProperties,
    ): DigitaalAdres =
        try {
            restClient(properties = properties)
                .get()
                .uri("$OK_DIGITALE_ADRESSEN_PATH/$digitaalAdresUuid")
                .retrieve()
                .body<DigitaalAdres>()
                ?: throw IllegalStateException("Error fetching DigitaalAdres: response body was null")
        } catch (e: HttpServerErrorException.InternalServerError) {
            handleInternalServerError(e)
        } catch (e: RestClientResponseException) {
            handleResponseException(e, "Error fetching DigitaalAdres with uuid: $digitaalAdresUuid")
        }

    fun updateDigitaalAdres(
        digitaalAdresUuid: UuidReference,
        patchData: DigitaalAdresPatchRequest,
        properties: OpenKlantProperties,
    ) = try {
        restClient(properties = properties)
            .patch()
            .uri("$OK_DIGITALE_ADRESSEN_PATH/$digitaalAdresUuid")
            .body(patchData)
            .retrieve()
            .body<DigitaalAdres>()
            ?: throw IllegalStateException("Error patching DigitaalAdres: response body was null")
    } catch (e: HttpServerErrorException.InternalServerError) {
        handleInternalServerError(e)
    } catch (e: RestClientResponseException) {
        handleResponseException(e, "Error patching DigitaalAdres with uuid: $digitaalAdresUuid")
    }

    fun getKlantcontacten(
        query: KlantcontactQuery,
        properties: OpenKlantProperties
    ): Page<Klantcontact> {
        if (query.bsn.isNullOrBlank() &&
            query.objectUuid.isNullOrBlank() &&
            query.partijUuid.isNullOrBlank()
        ) {
            return Page(count = 0, results = emptyList())
        }

        try {
            return restClient(properties = properties)
                .get()
                .uri { uriBuilder ->
                    buildKlantcontactUri(
                        builder = uriBuilder,
                        query = query
                    )
                }.retrieve()
                .body<Page<Klantcontact>>()
                ?: throw IllegalStateException("Error fetching Klantcontacten: response body was null")
        } catch (e: HttpServerErrorException.InternalServerError) {
            handleInternalServerError(e)
        } catch (e: RestClientResponseException) {
            handleResponseException(e, "Error fetching Klantcontacten")
        }
    }

    fun postKlantcontact(
        @Valid @RequestBody request: KlantcontactCreationRequest,
        properties: OpenKlantProperties,
    ) {
        try {
            restClient(properties = properties)
                .post()
                .uri(OK_MAAK_KLANTCONTACT_PATH)
                .body(request)
                .retrieve()
                .toBodilessEntity()
        } catch (e: HttpServerErrorException.InternalServerError) {
            handleInternalServerError(e)
        } catch (e: RestClientResponseException) {
            handleResponseException(e, "Error creating Klantcontact")
        }
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
                KlantcontactQueryParamNames.ONDERWERPOBJECT__ONDERWERPOBJECTIDENTIFICATORCODEOBJECTTYPE.value, it
            )
        }
        query.bsn?.let {
            builder.queryParam(
                KlantcontactQueryParamNames.HADBETROKKENE__WASPARTIJ__PARTIJIDENTIFICATOR__OBJECTID.value, it
            )
        }
        query.objectUuid?.let {
            builder.queryParam(
                KlantcontactQueryParamNames.ONDERWERPOBJECT__ONDERWERPOBJECTIDENTIFICATOROBJECTID.value, it
            )
        }
        query.partijUuid?.let {
            builder.queryParam(
                KlantcontactQueryParamNames.HADBETROKKENE__WASPARTIJ__UUID.value, it
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
    ): URI {
        query.queryParams.forEach { (key, value) ->
            builder.queryParam(key, value)
        }
        return builder
            .path(OK_DIGITALE_ADRESSEN_PATH)
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
        private const val OK_PARTIJEN_PATH = "partijen"
        private const val OK_KLANTCONTACTEN_PATH = "klantcontacten"
        private const val OK_DIGITALE_ADRESSEN_PATH = "digitaleadressen"
        private const val OK_MAAK_KLANTCONTACT_PATH = "maak-klantcontact"

        private val logger = KotlinLogging.logger { }
    }
}
