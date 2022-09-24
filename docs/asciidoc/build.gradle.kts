dependencies {
    compileOnly(project(":spi"))
    compileOnly(project(":integtest:pg"))
    compileOnly(VertxLibs.jdbc)
    compileOnly(VertxLibs.pgsql)
    compileOnly(VertxLibs.mysql)
    compileOnly(VertxLibs.rx2)
    compileOnly(VertxLibs.rx3)
    compileOnly(MutinyLibs.core)
    compileOnly(MutinyLibs.pgsql)

    implementation(VertxLibs.docgen)
    annotationProcessor(VertxLibs.docgen)
}

apply<antora.AntoraDocComponentPlugin>()
configure<antora.AntoraDocComponentExtension> {
    antoraType.set(antora.AntoraType.MODULE)
    javadocTitle.set("jOOQ.x ${project.version} API")
    javadocProjects.set(
        when (gradle) {
            is ExtensionAware -> ((gradle as ExtensionAware).extensions["PROJECT_POOL"] as Map<*, Array<String>>)["jooqx"]!!
            else              -> emptyArray()
        }.map(project::project)
    )
}

tasks {
    register<mdtoadoc.MdToAdocTask>("mdToAdoc") {
        inputFile.set(file(rootDir.resolve("CHANGELOG.md")))
        outputFolder.set(buildDir.resolve("antora/modules/ROOT/pages"))
        outputFileName.set("pg-changelog.adoc")
    }
    named("asciidoc") {
        dependsOn("mdToAdoc")
    }
}
