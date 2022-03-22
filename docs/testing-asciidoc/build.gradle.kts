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

apply<antora.AntoraPlugin>()
configure<antora.AntoraExtension> {
    antoraModule.set("testing")
    javadocInDir.set(project(":jooqx-core").tasks.named<Javadoc>("testFixturesJavadoc").map { it.destinationDir!! }.get())
}
