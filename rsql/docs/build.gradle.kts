dependencies {
    api(project(":rsql:jooq"))
    api(project(":sample:model"))

    implementation(VertxLibs.docgen)
    annotationProcessor(VertxLibs.docgen)
}

tasks {
    register<AsciiDocGenTask>("asciidoc")
    javadoc {
        dependsOn(withType<AsciiDocGenTask>())
    }
}
