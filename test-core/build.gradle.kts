plugins {
    id("Entropy.kotlin-common-conventions")
    id("com.ncorti.ktfmt.gradle") version "0.15.1"
    `java-library`
}

ktfmt { kotlinLangStyle() }

dependencies {
    implementation(project(":core"))
    implementation("org.junit.jupiter:junit-jupiter:5.9.2")
    implementation("io.mockk:mockk:1.13.4")
    implementation("io.kotest:kotest-assertions-core:5.5.4")
    implementation("com.github.alexburlton:swing-test:4.0.0")
}
