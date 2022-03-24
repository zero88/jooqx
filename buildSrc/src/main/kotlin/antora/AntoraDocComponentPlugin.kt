package antora

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.api.tasks.javadoc.Javadoc
import org.gradle.external.javadoc.StandardJavadocDocletOptions
import org.gradle.kotlin.dsl.*
import org.yaml.snakeyaml.Yaml
import java.io.FileWriter
import java.nio.file.Paths

class AntoraDocComponentPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        val ext = project.extensions.create<AntoraDocComponentExtension>("antora")
        project.afterEvaluate {
            if (ext.javadocInDir.isPresent && !ext.javadocProjects.orNull.isNullOrEmpty()) {
                throw IllegalArgumentException("Provide only one of javadocInDir or javadocProjects")
            }
            if (!project.findProperty("antoraOutDir")?.toString().isNullOrBlank()) {
                val p = Paths.get(project.findProperty("antoraOutDir").toString())
                ext.antoraOutDir.set(if (p.isAbsolute) p.toFile() else project.rootDir.toPath().resolve(p).toFile())
            }
        }
        project.tasks {
            register("antoraSetup") {
                group = "documentation"
                doLast {
                    project.copy {
                        from(ext.antoraSrcDir.get())
                        into(ext.antoraOutDir.get())
                    }
                    val yaml = Yaml()
                    val descriptorFile = AntoraDocComponentLayout.getDescriptor(ext.antoraOutDir).asFile
                    val map = yaml.load<Map<String, Any>>(descriptorFile.inputStream()).toMutableMap()
                    val docVersion = project.findProperty("docVersion")?.toString()
                    if (!docVersion.isNullOrBlank() || map["version"].toString().ifEmpty { "null" } == "null") {
                        map["version"] = docVersion ?: ext.docVersion.getOrElse(project.version.toString())
                    }
                    if (ext.asciiAttributes.orNull.isNullOrEmpty().not()) {
                        val asciidoc = map["asciidoc"] as? Map<*, *>
                        val attrs = (asciidoc?.get("attributes") as? Map<*, *>).orEmpty()
                        map["asciidoc"] =
                            asciidoc.orEmpty().plus(Pair("attributes", attrs.plus(ext.asciiAttributes.get())))
                    }
                    yaml.dump(map, FileWriter(descriptorFile))
                }
            }
            register<JavaCompile>("asciidoc") {
                group = "documentation"
                dependsOn("antoraSetup")
                val ss = project.the<JavaPluginExtension>().sourceSets.getByName(SourceSet.MAIN_SOURCE_SET_NAME)
                source = ss.java
                classpath = ss.compileClasspath
                destinationDirectory.set(ext.antoraLayout.map { it.getPages(ext.antoraSrcDir) })
                options.annotationProcessorPath = ss.compileClasspath
                options.compilerArgs = listOf(
                    "-proc:only",
                    "-processor",
                    "io.vertx.docgen.JavaDocGenProcessor",
                    "-Adocgen.output=${ext.antoraLayout.map { it.getPages(ext.antoraOutDir) }.get()}",
                    "-Adocgen.source=${ext.antoraLayout.map { it.getPages(ext.antoraSrcDir) }.get()}/*.adoc"
                )
            }
            named<Javadoc>("javadoc") {
                onlyIf {
                    !ext.javadocProjects.orNull.isNullOrEmpty()
                }
                dependsOn("asciidoc")
                options {
                    this as StandardJavadocDocletOptions
                    this.addBooleanOption("-no-module-directories", true)
                }
                doFirst {
                    val ss: List<SourceSet> = ext.javadocProjects.get()
                        .map { it.the<JavaPluginExtension>().sourceSets.getByName(SourceSet.MAIN_SOURCE_SET_NAME) }
                    if (ext.javadocTitle.isPresent) {
                        title = ext.javadocTitle.get()
                    }
                    source = ss.map { it.java.asFileTree }.reduceOrNull { acc, fileTree -> acc.plus(fileTree) }!!
                    classpath = project.files(ss.map { it.compileClasspath })
                    setDestinationDir(project.file("${project.buildDir}/docs/javadoc"))
                }
                doLast {
                    project.copy {
                        from("${project.buildDir}/docs/javadoc")
                        into(ext.antoraLayout.map { it.getAttachments(ext.antoraOutDir) }.get().dir("javadoc"))
                    }
                }
            }
            register("antora") {
                group = "documentation"
                dependsOn("javadoc")
                doFirst {
                    if (ext.javadocInDir.isPresent) {
                        project.copy {
                            from(ext.javadocInDir.get())
                            into(ext.antoraLayout.map { it.getAttachments(ext.antoraOutDir) }.get().dir("javadoc"))
                        }
                    }
                }
            }
            named("assemble") {
                dependsOn("antora")
            }
        }

    }
}
