plugins {
    `kotlin-dsl`
}

repositories {
    mavenLocal()
    gradlePluginPortal()
}

dependencies {
    api("org.testcontainers:testcontainers:1.19.7")
}
