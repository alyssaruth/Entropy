plugins {
    id("Entropy.kotlin-common-conventions")
    id("org.jlleitschuh.gradle.ktlint") version "11.6.1"
    `java-library`
}

dependencies {
    implementation("javax.mail:javax.mail-api:1.6.2")
    implementation("javax.activation:activation:1.1.1")
}
