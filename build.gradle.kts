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
    testImplementation("org.assertj:assertj-core:3.24.2")

}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(17)
}