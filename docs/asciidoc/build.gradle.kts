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
}

apply<antora.AntoraPlugin>()
configure<antora.AntoraExtension> {
    javadocTitle.set("jOOQ.x ${project.version} API")
    javadocProjects.set(projects.map { project.project(it) }.toList())
}
