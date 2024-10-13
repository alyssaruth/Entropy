import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent

plugins {
    id("Entropy.kotlin-common-conventions")
    id("com.ncorti.ktfmt.gradle") version "0.15.1"
    `java-library`
}

ktfmt { kotlinLangStyle() }

dependencies {
    implementation("javax.activation:activation:1.1.1")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.16.1")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.16.1")
    implementation("ch.qos.logback:logback-classic:1.5.8")
    implementation("net.logstash.logback:logstash-logback-encoder:8.0")
    testImplementation(project(":test-core"))
}

tasks.withType<Test> {
    testLogging {
        events = mutableSetOf(TestLogEvent.STARTED, TestLogEvent.FAILED)
        exceptionFormat = TestExceptionFormat.FULL
    }
}
