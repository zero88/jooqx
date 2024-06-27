import cloud.playio.gradle.shared.prop

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

val semanticVersion = prop(project, "semanticVersion", "")
val jvm8Version = when (semanticVersion) {
    "-SNAPSHOT" -> project.version.toString().replace(semanticVersion, "+jvm8$semanticVersion")
    else        -> "${project.version}+jvm8"
}

documentation {
    antora {
        asciiAttributes.set(
            mapOf(
                "jooqx-version" to project.version,
                "jooqx-jvm8-artifact" to jvm8Version,
                "jooq-version" to libs.jooq.get().version,
                "vertx-version" to libs.vertxCore.get().version,
                "mutiny-version" to libs.mutinyCore.get().version,
            )
        )
    }
}
