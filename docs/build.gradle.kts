@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.antora)
}

subprojects {
    oss {
        baseName.set("jooqx-${project.name}")
        title.set(baseName.map { "$it documentation" })
    }
}

documentation {
    antora {
        asciiAttributes.set(
            mapOf(
                "jooqx-version" to project.version,
                "vertx-version" to libs.versions.vertx,
                "jooq-version" to libs.versions.jooq,
                "mutiny-version" to MutinyLibs.Version.mutiny
            )
        )
    }
}
