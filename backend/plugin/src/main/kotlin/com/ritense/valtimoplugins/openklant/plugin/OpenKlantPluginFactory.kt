package com.ritense.valtimoplugins.openklant.plugin

import com.ritense.plugin.PluginFactory
import com.ritense.plugin.service.PluginService
import com.ritense.valtimoplugins.openklant.service.OpenKlantService
import com.ritense.valtimoplugins.openklant.util.ReflectionUtil

class OpenKlantPluginFactory(
    pluginService: PluginService,
    private val openKlantPluginService: OpenKlantService,
    private val reflectionUtil: ReflectionUtil,
    private val objectMapper: ObjectMapper
) : PluginFactory<OpenKlantPlugin>(pluginService) {
    override fun create() =
        OpenKlantPlugin(
            openKlantPluginService = openKlantPluginService,
            reflectionUtil = reflectionUtil,
            objectMapper = objectMapper
        )
}
