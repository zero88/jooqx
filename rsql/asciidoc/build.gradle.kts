import cloud.playio.gradle.antora.tasks.AntoraCopyTask
import cloud.playio.gradle.generator.docgen.AsciidocGenTask

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.antora)
    alias(libs.plugins.docgen)
}

dependencies {
    compileOnly(projects.rsql.jooq)
    compileOnly(projects.integtest.postgres)
    compileOnly(libs.jdbcVertx)
    compileOnly(libs.postgresVertx)
    compileOnly(libs.mysqlVertx)
    compileOnly(libs.vertxRx2)

    implementation(libs.jdbcVertx)
    implementation(libs.postgresVertx)
    implementation(libs.mysqlVertx)
    implementation(libs.vertxRx2)
}

documentation {
    antora {
        asciiAttributes.set(mapOf("rsql-version" to project.version))
        javadocTitle.set("jOOQ RSQL ${project.version} API")
        javadocProjects.set(
            when (gradle) {
                is ExtensionAware -> ((gradle as ExtensionAware).extensions["PROJECT_POOL"] as Map<*, Array<String>>)["rsql"]!!
                else              -> emptyArray()
            }.map(project::project)
        )
    }
}

tasks {
    named<AntoraCopyTask>("antoraPartials") {
        from(withType<AsciidocGenTask>())
        include("*.adoc")
    }
}

oss {
    title.set("RSQL documentation")
}
