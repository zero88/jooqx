package antora

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.api.tasks.javadoc.Javadoc
import org.gradle.external.javadoc.StandardJavadocDocletOptions
import org.gradle.kotlin.dsl.*

class AntoraPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        val ext = project.extensions.create<AntoraPluginExtension>("antora")
        project.tasks {
            register("antoraSetup") {
                group = "documentation"
                doLast {
                    project.copy {
                        from(ext.antoraSrcDir.get())
                        into(ext.antoraOutDir.get())
                    }
                }
            }
            register<JavaCompile>("asciidoc") {
                group = "documentation"
                dependsOn("antoraSetup")
                val ss = project.the<JavaPluginExtension>().sourceSets.getByName(SourceSet.MAIN_SOURCE_SET_NAME)
                val docSrc = ext.antoraSrcDir.dir(ext.antoraModulePage).get()
                val docOut = ext.antoraOutDir.dir(ext.antoraModulePage).get()
                source = ss.java
                classpath = ss.compileClasspath
                destinationDirectory.set(docSrc)
                options.annotationProcessorPath = ss.compileClasspath
                options.compilerArgs = listOf(
                    "-proc:only", "-processor",
                    "io.vertx.docgen.JavaDocGenProcessor",
                    "-Adocgen.output=$docOut",
                    "-Adocgen.source=${docSrc}/*.adoc"
                )
            }
            named<Javadoc>("javadoc") {
                onlyIf {
                    ext.javadocProjects.isPresent && ext.javadocProjects.get().isNotEmpty()
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
                        into("${project.buildDir}/antora/modules/ROOT/attachments/javadoc")
                    }
                }
            }
            named("assemble") {
                dependsOn("antora")
            }
        }

    }
}
