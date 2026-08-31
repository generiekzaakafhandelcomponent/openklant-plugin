/*
 * Copyright 2026 Ritense BV, the Netherlands.
 *
 * Licensed under EUPL, Version 1.2 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ritense.valtimoplugins.openklant.resolver

import com.fasterxml.jackson.databind.ObjectMapper
import com.ritense.plugin.service.PluginService
import com.ritense.valtimoplugins.openklant.BaseIntegrationTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional
import java.net.URI

private const val ENVIRONMENT_URL = "https://environment.klantinteracties.test"
private const val ENVIRONMENT_TOKEN = "environment-token"
private const val PLUGIN_URL = "https://plugin.klantinteracties.test"
private const val PLUGIN_TOKEN = "plugin-token"

/**
 * Starts the application without any Open Klant configuration. It should come up, and the resolver
 * should report the missing configuration rather than silently resolving nothing.
 */
internal class OpenKlantResolverWithoutConfigurationIT : BaseIntegrationTest() {
    @Autowired
    lateinit var valueResolverFactory: OpenKlantValueResolverFactory

    @Test
    fun `should report the missing configuration`() {
        val exception = assertThrows<IllegalStateException> { valueResolverFactory.openKlantProperties() }

        assertEquals(OpenKlantValueResolverFactory.MISSING_CONFIGURATION_MESSAGE, exception.message)
    }
}

/**
 * Starts the application with only the environment variables that configured the resolver before it
 * read the plugin configuration.
 */
@SpringBootTest(
    properties = [
        "AUTODEPLOYMENT_PLUGINCONFIG_OPENKLANT_KLANTINTERACTIES_URL=https://environment.klantinteracties.test",
        "AUTODEPLOYMENT_PLUGINCONFIG_OPENKLANT_AUTHORIZATION_TOKEN=environment-token",
    ],
)
internal class OpenKlantResolverWithEnvironmentVariablesIT : BaseIntegrationTest() {
    @Autowired
    lateinit var valueResolverFactory: OpenKlantValueResolverFactory

    @Test
    fun `should fall back to the environment variables`() {
        val properties = valueResolverFactory.openKlantProperties()

        assertEquals(URI.create(ENVIRONMENT_URL), properties.klantinteractiesUrl)
        assertEquals(ENVIRONMENT_TOKEN, properties.token)
    }
}

/**
 * Starts the application with the environment variables set and a plugin configuration deployed.
 * The plugin configuration wins.
 */
@SpringBootTest(
    properties = [
        "AUTODEPLOYMENT_PLUGINCONFIG_OPENKLANT_KLANTINTERACTIES_URL=https://environment.klantinteracties.test",
        "AUTODEPLOYMENT_PLUGINCONFIG_OPENKLANT_AUTHORIZATION_TOKEN=environment-token",
    ],
)
internal class OpenKlantResolverWithPluginConfigurationIT : BaseIntegrationTest() {
    @Autowired
    lateinit var valueResolverFactory: OpenKlantValueResolverFactory

    @Autowired
    lateinit var pluginService: PluginService

    @Test
    @Transactional
    fun `should let the plugin configuration override the environment variables`() {
        createPluginConfiguration("Open Klant")

        val resolved = valueResolverFactory.openKlantProperties()

        assertEquals(URI.create(PLUGIN_URL), resolved.klantinteractiesUrl)
        assertEquals(PLUGIN_TOKEN, resolved.token)
    }

    @Test
    @Transactional
    fun `should report multiple plugin configurations instead of picking one`() {
        createPluginConfiguration("Open Klant A")
        createPluginConfiguration("Open Klant B")

        val exception = assertThrows<IllegalStateException> { valueResolverFactory.openKlantProperties() }

        assertTrue(
            exception.message!!.startsWith(OpenKlantValueResolverFactory.MULTIPLE_CONFIGURATIONS_MESSAGE),
            "Unexpected message: ${exception.message}",
        )
        assertTrue(exception.message!!.contains("Open Klant A"), "Unexpected message: ${exception.message}")
        assertTrue(exception.message!!.contains("Open Klant B"), "Unexpected message: ${exception.message}")
    }

    private fun createPluginConfiguration(title: String) {
        val properties =
            ObjectMapper().createObjectNode().apply {
                put("klantinteractiesUrl", PLUGIN_URL)
                put("token", PLUGIN_TOKEN)
            }
        pluginService.createPluginConfiguration(title, properties, "openklant")
    }
}
