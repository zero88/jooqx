dependencies {
    compileOnly(project(":rsql:jooq"))
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
    asciiAttributes.set(mapOf("rsql-version" to project.version))
    javadocTitle.set("jOOQ RSQL ${project.version} API")
    javadocProjects.set(
        when (gradle) {
            is ExtensionAware -> ((gradle as ExtensionAware).extensions["PROJECT_POOL"] as Map<*, Array<String>>)["rsql"]!!
            else              -> emptyArray()
        }.map(project::project)
    )
}
