plugins {
    id("Entropy.kotlin-common-conventions")
    id("com.ncorti.ktfmt.gradle") version "0.15.1"
    `java-library`
    `java-test-fixtures`
}

ktfmt { kotlinLangStyle() }

dependencies {
    implementation("javax.mail:javax.mail-api:1.6.2")
    implementation("javax.activation:activation:1.1.1")
}
