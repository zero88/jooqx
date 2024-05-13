import cloud.playio.gradle.antora.AntoraType
import cloud.playio.gradle.antora.tasks.AntoraCopyTask
import cloud.playio.gradle.generator.docgen.AsciidocGenTask
import cloud.playio.gradle.pandoc.FormatFrom
import cloud.playio.gradle.pandoc.FormatTo
import cloud.playio.gradle.pandoc.tasks.PandocTask

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.docgen)
    // Not yet known why alias doesn't work. https://github.com/gradle/gradle/issues/20084
    id(libs.plugins.antora.get().pluginId)
    id(libs.plugins.pandoc.get().pluginId)
}

dependencies {
    compileOnly(projects.spi)
    compileOnly(projects.integtest.postgres)
    compileOnly(libs.jooqMeta)
    compileOnly(libs.jdbcVertx)
    compileOnly(libs.postgresVertx)
    compileOnly(libs.postgresMutiny)
    compileOnly(libs.mysqlVertx)
    compileOnly(libs.vertxRx2)
    compileOnly(libs.vertxRx3)
    compileOnly(libs.mutinyCore)
}

documentation {
    antora {
        antoraType.set(AntoraType.MODULE)
        javadocTitle.set("jOOQ.x ${project.version} API")
        javadocProjects.set(
            when (gradle) {
                is ExtensionAware -> ((gradle as ExtensionAware).extensions["PROJECT_POOL"] as Map<*, Array<String>>)["jooqx"]!!
                else              -> emptyArray()
            }.map(project::project)
        )
    }

    pandoc {
        from.set(FormatFrom.markdown)
        to.set(FormatTo.asciidoc)
        inputFile.set(rootDir.resolve("CHANGELOG.md"))
        outFile.set("pg-changelog.adoc")
        config {
            arguments.set(arrayOf("--trace"))
        }
    }
}

tasks {
    named<AntoraCopyTask>("antoraPages") { from(withType<PandocTask>()) }
    named<AntoraCopyTask>("antoraPartials") {
        from(withType<AsciidocGenTask>())
        include("*.adoc")
    }
    afterEvaluate {
        withType<JavaCompile>().configureEach {
            javaCompiler.set(javaToolchains.compilerFor {
                languageVersion.set(JavaLanguageVersion.of(17))
            })
        }
    }
}
