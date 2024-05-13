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
                "jooq-version" to libs.jooq.get().version,
                "vertx-version" to libs.vertxCore.get().version,
                "mutiny-version" to libs.mutinyCore.get().version,
            )
        )
    }
}
