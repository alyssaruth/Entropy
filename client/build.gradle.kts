plugins {
    id("Entropy.kotlin-common-conventions")
    id("org.jlleitschuh.gradle.ktlint") version "11.6.1"
    application
}

dependencies {
    implementation("com.jgoodies:jgoodies-forms:1.6.0")
    implementation("com.miglayout:miglayout-swing:5.2")
    implementation(project(":core"))
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
