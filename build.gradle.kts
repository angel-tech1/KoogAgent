group = "com.github.com.angeltech"
version = "1.0-SNAPSHOT"

plugins {
    // Applies the Kotlin JVM plugin
    kotlin("jvm") version "2.3.21"
}

repositories {
    mavenCentral()
}

sourceSets.main {
    java.srcDirs("src/main/java", "src/main/kotlin")
}

dependencies {
    implementation("ai.koog:koog-agents:0.7.1")

    // SLF4J API
    implementation("org.slf4j:slf4j-api:2.0.9")
    implementation("ch.qos.logback:logback-classic:1.5.13")

    testImplementation(kotlin("test"))
}
