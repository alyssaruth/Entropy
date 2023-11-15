plugins {
    id("Entropy.kotlin-common-conventions")
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
