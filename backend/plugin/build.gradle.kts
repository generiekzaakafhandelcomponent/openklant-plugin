val kotlinLoggingMicroutilsVersion: String by project
val kotlinxCoroutinesVersion: String by project
val mockkVersion: String by project
val reactorNettyVersion: String by project
val springWebfluxVersion: String by project

dockerCompose {
    setProjectName("openklant")
    isRequiredBy(project.tasks.integrationTesting)

    tasks.integrationTesting {
        useComposeFiles.addAll("$rootDir/docker-resources/docker-compose-base-test.yml", "docker-compose-override.yml")
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
    testImplementation("com.ritense.valtimo:case")
    testImplementation("com.ritense.valtimo:process-document")
    testImplementation("com.ritense.valtimo:value-resolver")
    testImplementation("com.ritense.valtimo:zaken-api")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("io.mockk:mockk:$mockkVersion")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test")
}

apply(from = "gradle/publishing.gradle")
