plugins {
    id("Entropy.kotlin-common-conventions")
    id("com.ncorti.ktfmt.gradle") version DependencyVersions.KTFMT
    `java-library`
}

ktfmt { kotlinLangStyle() }

dependencies {
    implementation(project(":core"))
    implementation("org.junit.jupiter:junit-jupiter:5.9.2")
    implementation("io.mockk:mockk:1.13.4")
    implementation("io.kotest:kotest-assertions-core:5.5.4")
    implementation("com.github.alexburlton:swing-test:4.0.0")
    implementation("org.skyscreamer:jsonassert:1.5.3")
    implementation("ch.qos.logback:logback-classic:1.5.8")
    implementation("net.logstash.logback:logstash-logback-encoder:8.0")
}
