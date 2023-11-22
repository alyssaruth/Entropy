plugins {
    id("Entropy.kotlin-common-conventions")
    id("org.jlleitschuh.gradle.ktlint") version "11.6.1"
    application
}

dependencies {
    implementation("javax.mail:javax.mail-api:1.6.2")
    implementation(project(":core"))
}

application {
    // Define the main class for the application.
    mainClass.set("server.EntropyServer")
}
