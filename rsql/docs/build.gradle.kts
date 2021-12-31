qwe {
    application.set(false)
}

dependencies {
    implementation(VertxLibs.docgen)
    annotationProcessor(VertxLibs.docgen)

    implementation(project(":rsql:jooq"))
    implementation(testFixtures(project(":integtest")))
}

tasks {
    register<AsciiDocGenTask>("asciidoc")
    javadoc {
        dependsOn(withType<AsciiDocGenTask>())
    }
}
