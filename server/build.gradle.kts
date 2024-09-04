plugins {
    id("Entropy.kotlin-common-conventions")
    id("com.ncorti.ktfmt.gradle") version "0.15.1"
    id("io.ktor.plugin") version "2.3.12"
    application
}

ktfmt { kotlinLangStyle() }

dependencies {
    implementation(project(":core"))
    implementation("io.ktor:ktor-server-core-jvm")
    implementation("io.ktor:ktor-server-content-negotiation-jvm")
    implementation("io.ktor:ktor-serialization-jackson-jvm")
    implementation("io.ktor:ktor-server-call-logging-jvm")
    implementation("io.ktor:ktor-server-call-id-jvm")
    implementation("io.ktor:ktor-server-host-common-jvm")
    implementation("io.ktor:ktor-server-status-pages-jvm")
    implementation("io.ktor:ktor-server-sessions-jvm")
    implementation("io.ktor:ktor-server-netty-jvm")
    implementation("io.ktor:ktor-server-config-yaml")
    testImplementation("io.ktor:ktor-server-test-host-jvm")
    testImplementation(project(":test-core"))
}

application {
    // Define the main class for the application.
    mainClass.set("io.ktor.server.netty.EngineMain")
}
