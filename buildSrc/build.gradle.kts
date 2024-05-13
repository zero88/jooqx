plugins {
    `kotlin-dsl`
}

repositories {
    mavenLocal()
    gradlePluginPortal()
}

dependencies {
    implementation(libs.junit5Container)
}
