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
    implementation(project(":core"))
    testImplementation(project(":test-core"))
}

application {
    // Define the main class for the application.
    mainClass.set("EntropyMain")
}

task<JavaExec>("runDev") {
    configure(
        closureOf<JavaExec> {
            group = "run"
            classpath = project.the<SourceSetContainer>()["main"].runtimeClasspath
            args = listOf("devMode")
        }
    )
}
