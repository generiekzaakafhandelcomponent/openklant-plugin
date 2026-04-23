val kotlinLoggingMicroutilsVersion: String by project
val kotlinxCoroutinesVersion: String by project
val mockkVersion: String by project
val reactorNettyVersion: String by project
val springWebfluxVersion: String by project

dockerCompose {
    setProjectName("openklant")
    isRequiredBy(project.tasks.integrationTesting)

    tasks.integrationTesting {
        useComposeFiles.addAll("$rootDir/docker-resources/docker-compose-base-test.yml")
    }
}

dependencies {
    compileOnly("com.ritense.valtimo:core")
    compileOnly("com.ritense.valtimo:plugin-valtimo")
    compileOnly("com.ritense.valtimo:valtimo-gzac-dependencies")
    compileOnly("org.springframework:spring-webflux:$springWebfluxVersion")
    implementation("io.projectreactor.netty:reactor-netty-core:$reactorNettyVersion")
    implementation("io.projectreactor.netty:reactor-netty-http:$reactorNettyVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinxCoroutinesVersion")
    compileOnly("io.github.microutils:kotlin-logging:$kotlinLoggingMicroutilsVersion")

    // Testing
    testImplementation("com.ritense.valtimo:building-block")
    testImplementation("com.ritense.valtimo:contract")
    testImplementation("com.ritense.valtimo:core")
    testImplementation("com.ritense.valtimo:plugin")
    testImplementation("com.ritense.valtimo:temporary-resource-storage")
    testImplementation("com.ritense.valtimo:test-utils-common")

    testImplementation("org.springframework.boot:spring-boot-starter-test")

    testImplementation("org.postgresql:postgresql")

    testImplementation("com.ritense.valtimo:plugin-valtimo")
    testImplementation("com.ritense.valtimo:valtimo-gzac-dependencies")

    testImplementation("io.mockk:mockk:$mockkVersion")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test")
}

apply(from = "gradle/publishing.gradle")
