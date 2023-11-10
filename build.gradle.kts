plugins {
    kotlin("jvm") version "1.9.0"
}

group = "tech.qmates"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.eclipse.jetty:jetty-server:11.0.18")
    implementation("org.eclipse.jetty:jetty-servlet:11.0.18")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(17)
}