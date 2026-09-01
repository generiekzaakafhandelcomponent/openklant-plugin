val jetbrainsAnnotationsVersion: String by project
val kotlinLoggingVersion: String by project
val mockkVersion: String by project
val okhttpVersion: String by project
val operatonVersion: String by project

dockerCompose {
    setProjectName("openklant")
    isRequiredBy(project.tasks.test)

    tasks.test {
        useComposeFiles.addAll("$rootDir/docker-resources/docker-compose-base-test.yml")
    }
}

dependencies {
    compileOnly("com.ritense.valtimo:core")
    compileOnly("com.ritense.valtimo:plugin-valtimo")
    compileOnly("com.ritense.valtimo:valtimo-gzac-dependencies")

    compileOnly("io.github.oshai:kotlin-logging:$kotlinLoggingVersion")
    compileOnly("org.jetbrains:annotations:$jetbrainsAnnotationsVersion")
    compileOnly("org.operaton.bpm:operaton-engine:$operatonVersion")

    compileOnly("org.springframework.boot:spring-boot-starter-security")
    compileOnly("org.springframework.boot:spring-boot-starter-validation")
    compileOnly("org.springframework.boot:spring-boot-starter-web")

    // Testing
    testImplementation("com.ritense.valtimo:core")
    testImplementation("com.ritense.valtimo:plugin-valtimo")
    testImplementation("com.ritense.valtimo:valtimo-gzac-dependencies")

    testImplementation("io.github.oshai:kotlin-logging:$kotlinLoggingVersion")
    testImplementation("org.jetbrains:annotations:$jetbrainsAnnotationsVersion")
    testImplementation("org.operaton.bpm:operaton-engine:$operatonVersion")

    testImplementation("org.springframework.boot:spring-boot-starter-security")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.boot:spring-boot-starter-validation")
    testImplementation("org.springframework.boot:spring-boot-starter-web")

    testImplementation("com.squareup.okhttp3:mockwebserver:$okhttpVersion")
    testImplementation("io.mockk:mockk:$mockkVersion")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test")
    testImplementation("org.postgresql:postgresql")
}

apply(from = "gradle/publishing.gradle")
