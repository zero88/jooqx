plugins {
    `kotlin-dsl`
}

repositories {
    mavenLocal()
    gradlePluginPortal()
    //    https://github.com/bodiam/markdown-to-asciidoc/issues/26#issuecomment-954747922
    maven { setUrl("https://jitpack.io") }
}

dependencies {
    implementation("org.yaml:snakeyaml:1.30")
    implementation("com.github.bodiam:markdown-to-asciidoc:2.0")
}
