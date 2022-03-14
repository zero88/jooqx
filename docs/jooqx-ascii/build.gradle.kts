dependencies {
    compileOnly(project(":spi"))
    compileOnly(project(":sample:model"))
    compileOnly(VertxLibs.jdbc)
    compileOnly(VertxLibs.pgsql)
    compileOnly(VertxLibs.mysql)
    compileOnly(VertxLibs.rx2)

    implementation(VertxLibs.docgen)
    annotationProcessor(VertxLibs.docgen)
}

tasks {
    register<AsciiDocGenTask>("asciidoc")
    javadoc {
        dependsOn(withType<AsciiDocGenTask>())
    }
}
