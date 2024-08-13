plugins {
    id("Entropy.kotlin-common-conventions")
    id("com.ncorti.ktfmt.gradle") version "0.15.1"
    application
}

ktfmt { kotlinLangStyle() }

dependencies {
    implementation("javax.mail:javax.mail-api:1.6.2")
    implementation(project(":core"))
}

application {
    // Define the main class for the application.
    mainClass.set("server.EntropyServer")
}
