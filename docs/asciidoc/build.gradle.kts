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
val projects = if (gradle is ExtensionAware) {
    ((gradle as ExtensionAware).extensions["PROJECT_POOL"] as Map<String, Array<String>>)["jooqx"]!!
} else {
    emptyArray()
}.map { project.project(it) }

apply<antora.AntoraPlugin>()
configure<antora.AntoraExtension> {
    asciiAttributes.set(
        mapOf(
            "jooqx-version" to project.version,
            "vertx-version" to VertxLibs.Version.vertx,
            "jooq-version" to DatabaseLibs.Version.jooq
        )
    )
    javadocTitle.set("jOOQ.x ${project.version} API")
    javadocProjects.set(projects)
}
