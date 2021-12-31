qwe {
    application.set(false)
}

dependencies {
    implementation(VertxLibs.docgen)
    annotationProcessor(VertxLibs.docgen)

    implementation(project(":spi"))
    implementation(testFixtures(project(":integtest")))
    implementation(VertxLibs.jdbc)
    implementation(VertxLibs.pgsql)
    implementation(VertxLibs.mysql)
    implementation(VertxLibs.rx2)
}

tasks {
    register<AsciiDocGenTask>("asciidoc")
    javadoc {
        dependsOn(withType<AsciiDocGenTask>())
    }
}
