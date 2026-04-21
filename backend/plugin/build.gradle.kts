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
    implementation("com.ritense.valtimo:core")
    implementation("com.ritense.valtimo:plugin-valtimo")
    implementation("com.ritense.valtimo:valtimo-gzac-dependencies")
    implementation("org.springframework:spring-webflux:$springWebfluxVersion")
    implementation("io.projectreactor.netty:reactor-netty-core:$reactorNettyVersion")
    implementation("io.projectreactor.netty:reactor-netty-http:$reactorNettyVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinxCoroutinesVersion")
    implementation("io.github.microutils:kotlin-logging:$kotlinLoggingMicroutilsVersion")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("io.mockk:mockk:$mockkVersion")
}

apply(from = "gradle/publishing.gradle")