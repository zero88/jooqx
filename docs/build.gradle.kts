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

tasks.register<JavaCompile>("docProcessing") {
    group = "documentation"
    source = sourceSets.getByName(SourceSet.MAIN_SOURCE_SET_NAME).java
    destinationDir = project.file("${projectDir}/src/asciidoc")
    classpath = configurations.compileClasspath.get()
    options.annotationProcessorPath = configurations.compileClasspath.get()
    options.compilerArgs = listOf(
        "-proc:only",
        "-processor", "io.vertx.docgen.JavaDocGenProcessor",
        "-Adocgen.output=${project.buildDir}/asciidoc",
        "-Adocgen.source=${project.projectDir}/src/asciidoc/jooqx.adoc"
    )
}

tasks.compileJava {
    dependsOn(tasks.named("docProcessing"))
}
