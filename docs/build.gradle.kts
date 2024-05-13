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
                "vertx-version" to libs.versions.vertx.get(),
                "mutiny-version" to libs.versions.mutiny.get(),
                "jooq-version" to libs.versions.jooq.jdk17.get(),
            )
        )
    }
}
