package com.ritense.valtimoplugins.openklant.controller

import com.ritense.plugin.service.PluginService
import com.ritense.valtimoplugins.openklant.dto.DigitaalAdres
import com.ritense.valtimoplugins.openklant.dto.DigitaalAdresCreationRequest
import com.ritense.valtimoplugins.openklant.plugin.OpenKlantPlugin
import jakarta.validation.Valid
import mu.KotlinLogging
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException

// TODO:
// - Throw any validation error (400 bad request) from the Open Klant API: keep governance in that API.
// - Fix authentication

@Validated
@RestController
@RequestMapping("/api/open-klant/v1/{pluginConfigurationId}")
class OpenKlantController(
    private val pluginService: PluginService,
) {
    @GetMapping("/ping")
    fun ping(): ResponseEntity<String> = ResponseEntity.ok("Pong!")

    @PostMapping("/digitale-adressen")
    fun createDigitaalAdres(
        @PathVariable pluginConfigurationId: String, // Gets this from the class' RequestMapping
        @Valid @RequestBody digitaalAdresCreationRequest: DigitaalAdresCreationRequest,
    ): ResponseEntity<DigitaalAdres> {
        val pluginConfigurations = pluginService.findPluginConfigurations(OpenKlantPlugin::class.java)

        val openKlantPlugin: OpenKlantPlugin = pluginService.createInstance(pluginConfigurationId)

        return try {
            val digitaalAdres = openKlantPlugin.setDefaultDigitaalAdres(digitaalAdresCreationRequest)
            ResponseEntity.ok(digitaalAdres)
        } catch (e: IllegalArgumentException) {
            logger.warn { "Invalid request: ${e.message}" }
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid request", e)
        } catch (e: Exception) {
            logger.warn { "Failed to create DigitaalAdres: ${e.message}" }
            throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to create DigitaalAdres", e)
        }
    }

    companion object {
        private val logger = KotlinLogging.logger { }
    }
}
