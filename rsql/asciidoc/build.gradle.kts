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

val projects = if (gradle is ExtensionAware) {
    ((gradle as ExtensionAware).extensions["PROJECT_POOL"] as Map<String, Array<String>>)["rsql"]!!
} else {
    emptyArray()
}

apply<antora.AntoraPlugin>()
configure<antora.AntoraExtension> {
    javadocTitle.set("jOOQ RSQL ${project.version} API")
    javadocProjects.set(projects.map { project.project(it) }.toList())
}
