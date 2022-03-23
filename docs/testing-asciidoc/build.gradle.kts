dependencies {
    compileOnly(project(":spi"))
    compileOnly(testFixtures(project(":jooqx-core")))
//    compileOnly(project(":rsql:jooq"))
    compileOnly(project(":sample:model"))
    compileOnly(VertxLibs.jdbc)
    compileOnly(VertxLibs.pgsql)
    compileOnly(VertxLibs.mysql)
    compileOnly(VertxLibs.rx2)

    implementation(VertxLibs.docgen)
    annotationProcessor(VertxLibs.docgen)

    implementation(VertxLibs.jdbc)
    implementation(VertxLibs.pgsql)
    implementation(VertxLibs.mysql)
    implementation(VertxLibs.rx2)
}

apply<antora.AntoraDocComponentPlugin>()
configure<antora.AntoraDocComponentExtension> {
    antoraModule.set("testing")
    asciiAttributes.set(
        mapOf(
            "jooqx-version" to project.version,
            "vertx-version" to VertxLibs.Version.vertx,
            "jooq-version" to DatabaseLibs.Version.jooq
        )
    )
    javadocInDir.set(project(":jooqx-core").tasks.named<Javadoc>("testFixturesJavadoc").map { it.destinationDir!! }.get())
}
