plugins {
    id("java")
}

group = "com.github.dekmetzi"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of("21")
    }
}

var junit5Version = "5.11.3"

dependencies {
    implementation("org.slf4j:slf4j-api:2.0.16")
    implementation("ch.qos.logback:logback-classic:1.5.12")

    testImplementation("org.junit.jupiter:junit-jupiter:${junit5Version}")
    testImplementation("org.assertj:assertj-core:3.26.3")

    // required for gradle 9+
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}