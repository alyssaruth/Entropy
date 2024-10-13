import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent

plugins {
    id("Entropy.kotlin-common-conventions")
    id("com.ncorti.ktfmt.gradle") version "0.15.1"
    id("io.ktor.plugin") version DependencyVersions.KTOR
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
    implementation("ch.qos.logback:logback-classic:${DependencyVersions.LOGBACK}")
    testImplementation("io.ktor:ktor-server-test-host-jvm")
    testImplementation(project(":test-core"))
}

application {
    // Define the main class for the application.
    mainClass.set("io.ktor.server.netty.EngineMain")
}

tasks.withType<Test> {
    testLogging {
        events = mutableSetOf(TestLogEvent.STARTED, TestLogEvent.FAILED)
        exceptionFormat = TestExceptionFormat.FULL
    }
}
