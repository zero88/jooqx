import cloud.playio.gradle.antora.AntoraType
import cloud.playio.gradle.antora.tasks.AntoraCopyTask
import cloud.playio.gradle.generator.docgen.AsciidocGenTask
import cloud.playio.gradle.pandoc.FormatFrom
import cloud.playio.gradle.pandoc.FormatTo
import cloud.playio.gradle.pandoc.tasks.PandocTask

plugins {
    id(PlayioPlugin.antora)
    id(PlayioPlugin.pandoc)
    id(PlayioPlugin.docgen)
}

dependencies {
    compileOnly(project(":spi"))
    compileOnly(project(":integtest:postgres"))
    compileOnly(JooqLibs.jooqMeta)
    compileOnly(VertxLibs.jdbc)
    compileOnly(VertxLibs.pgsql)
    compileOnly(VertxLibs.mysql)
    compileOnly(VertxLibs.rx2)
    compileOnly(VertxLibs.rx3)
    compileOnly(MutinyLibs.core)
    compileOnly(MutinyLibs.pgsql)
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
