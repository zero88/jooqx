subprojects {
    oss {
        baseName.set("jooqx-${project.name}")
        title.set(baseName)
    }
}

apply<antora.AntoraDocComponentPlugin>()
configure<antora.AntoraDocComponentExtension> {
    antoraSrcDir.set("antora")
    asciiAttributes.set(
        mapOf(
            "jooqx-version" to project.version,
            "vertx-version" to VertxLibs.Version.vertx,
            "jooq-version" to DatabaseLibs.Version.jooq
        )
    )
}
