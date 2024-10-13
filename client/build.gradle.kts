import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent

plugins {
    id("Entropy.kotlin-common-conventions")
    id("com.ncorti.ktfmt.gradle") version "0.15.1"
    application
}

ktfmt { kotlinLangStyle() }

dependencies {
    implementation("com.jgoodies:jgoodies-forms:1.6.0")
    implementation("com.miglayout:miglayout-swing:5.2")
    implementation("com.konghq:unirest-java:3.14.2")
    implementation("ch.qos.logback:logback-classic:${DependencyVersions.LOGBACK}")
    implementation("com.fasterxml.jackson.core:jackson-databind:${DependencyVersions.JACKSON}")
    implementation(
        "com.fasterxml.jackson.module:jackson-module-kotlin:${DependencyVersions.JACKSON}"
    )
    implementation(project(":core"))
    testImplementation(project(":test-core"))
    testImplementation("com.squareup.okhttp3:mockwebserver:4.12.0")
}

application {
    // Define the main class for the application.
    mainClass.set("EntropyMain")
}

task<JavaExec>("runDev") {
    configure(
        closureOf<JavaExec> {
            group = "application"
            classpath = project.the<SourceSetContainer>()["main"].runtimeClasspath
            mainClass = "EntropyMain"
            args = listOf("devMode")
        }
    )
}

tasks.withType<Test> {
    testLogging {
        events = mutableSetOf(TestLogEvent.STARTED, TestLogEvent.FAILED)
        exceptionFormat = TestExceptionFormat.FULL
    }
}
