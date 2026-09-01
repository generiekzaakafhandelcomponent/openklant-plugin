package com.ritense.valtimoplugins.openklant.plugin

import com.fasterxml.jackson.databind.ObjectMapper
import com.ritense.plugin.annotation.PluginAction
import com.ritense.plugin.annotation.PluginActionProperty
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import java.nio.file.Path
import javax.xml.parsers.DocumentBuilderFactory
import kotlin.io.path.exists
import kotlin.io.path.name
import kotlin.io.path.walk

/** The sandbox application autodeploys the BPMN processes and process links under backend/app. */
internal class ProcessLinkFixtureTest {
    private val objectMapper = ObjectMapper()

    private val actionProperties: Map<String, Set<String>> =
        OpenKlantPlugin::class.java.methods
            .mapNotNull { method ->
                method.getAnnotation(PluginAction::class.java)?.let { action ->
                    action.key to
                        method.parameters
                            .filter { it.isAnnotationPresent(PluginActionProperty::class.java) }
                            .map { it.name }
                            .toSet()
                }
            }.toMap()

    @Test
    fun `finds the sandbox application configuration`() {
        assertTrue(configDirectory.exists(), "expected the sandbox config at $configDirectory")
        assertTrue(processLinkFiles().isNotEmpty(), "expected process link fixtures in $configDirectory")
    }

    @TestFactory
    fun `every process link refers to an existing plugin action and property`(): List<DynamicTest> =
        processLinkFiles().map { file ->
            DynamicTest.dynamicTest(file.name) {
                objectMapper.readTree(file.toFile()).forEach { link ->
                    if (link.path("processLinkType").asText() != "plugin") {
                        return@forEach
                    }
                    val key = link.path("pluginActionDefinitionKey").asText()
                    val properties = actionProperties[key]
                    assertTrue(properties != null, "unknown plugin action '$key' in ${file.name}")
                    link.path("actionProperties").fieldNames().forEach { property ->
                        assertTrue(
                            property in properties!!,
                            "action '$key' has no property '$property' in ${file.name}. " +
                                "Supported: ${properties.sorted()}",
                        )
                    }
                }
            }
        }

    @TestFactory
    fun `every process link refers to an activity of the matching BPMN process`(): List<DynamicTest> =
        processLinkFiles()
            .filter { bpmnFileFor(it).exists() }
            .map { file ->
                DynamicTest.dynamicTest(file.name) {
                    val bpmn = bpmnFileFor(file)
                    val document =
                        DocumentBuilderFactory
                            .newInstance()
                            .apply { isNamespaceAware = true }
                            .newDocumentBuilder()
                            .parse(bpmn.toFile())
                    val allElements = document.getElementsByTagName("*")
                    val activityIds =
                        (0 until allElements.length)
                            .mapNotNull {
                                allElements
                                    .item(it)
                                    .attributes
                                    ?.getNamedItem("id")
                                    ?.nodeValue
                            }.toSet()

                    objectMapper.readTree(file.toFile()).forEach { link ->
                        val activityId = link.path("activityId").asText()
                        assertTrue(
                            activityId in activityIds,
                            "process link points at activity '$activityId', which ${bpmn.name} does not contain",
                        )
                    }
                }
            }

    @OptIn(kotlin.io.path.ExperimentalPathApi::class)
    private fun processLinkFiles(): List<Path> =
        configDirectory
            .walk()
            .filter { it.name.endsWith(".process-link.json") }
            .sortedBy { it.name }
            .toList()

    private fun bpmnFileFor(processLink: Path): Path =
        processLink.parent.parent
            .resolve("bpmn")
            .resolve(processLink.name.removeSuffix(".process-link.json") + ".bpmn")

    private companion object {
        /** Resolved from the module directory, which Gradle uses as the working directory for tests. */
        val configDirectory: Path =
            generateSequence(Path.of("").toAbsolutePath()) { it.parent }
                .map { it.resolve("backend/app/src/main/resources/config") }
                .first { it.exists() }
    }
}
