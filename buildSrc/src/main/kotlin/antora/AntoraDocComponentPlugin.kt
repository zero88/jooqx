package antora

import antora.tasks.AntoraDescriptorTask
import antora.tasks.AntoraInitTask
import antora.tasks.AntoraTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.Directory
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.api.tasks.javadoc.Javadoc
import org.gradle.external.javadoc.StandardJavadocDocletOptions
import org.gradle.kotlin.dsl.*
import java.nio.file.Paths

class AntoraDocComponentPlugin : Plugin<Project> {

    override fun apply(project: Project): Unit = project.run {
        val ext = project.extensions.create<AntoraDocComponentExtension>("antora")
        lateinit var srcAntora: AntoraCompLayout
        lateinit var destAntora: AntoraCompLayout
        project.afterEvaluate {
            if (!ext.javadocInDir.isEmpty && !ext.javadocProjects.orNull.isNullOrEmpty()) {
                throw IllegalArgumentException("Provide only one of javadocInDir or javadocProjects")
            }
            if (!project.findProperty("antoraOutDir")?.toString().isNullOrBlank()) {
                val p = Paths.get(project.findProperty("antoraOutDir").toString())
                ext.antoraOutDir.set(if (p.isAbsolute) p.toString() else project.rootDir.toPath().resolve(p).toString())
            }
            srcAntora = AntoraCompLayout.create(
                project.layout.projectDirectory.dir(ext.antoraSrcDir).get(),
                ext.antoraModule.get()
            )
            destAntora = AntoraCompLayout.create(
                project.layout.buildDirectory.dir(ext.antoraOutDir).get(),
                ext.antoraModule.get()
            )
        }
        project.tasks {
            register<AntoraInitTask>(AntoraInitTask.NAME) {
                inputDir.set(srcAntora.getDir())
                outputDir.set(destAntora.getDir())
                antoraModule.set(ext.antoraModule)
                antoraType.set(ext.antoraType)
            }
            register<AntoraDescriptorTask>(AntoraDescriptorTask.NAME) {
                dependsOn(AntoraInitTask.NAME)
                onlyIf { ext.antoraType.orNull?.isComponent() == true }
                inputFile.set(srcAntora.descriptorFile())
                outputFile.set(destAntora.descriptorFile())
                docVersion.set(ext.docVersion)
                asciiAttributes.set(ext.asciiAttributes)
            }
            register<JavaCompile>("asciidoc") {
                group = "documentation"
                dependsOn(AntoraDescriptorTask.NAME)
                val ss = project.the<JavaPluginExtension>().sourceSets.getByName(SourceSet.MAIN_SOURCE_SET_NAME)
                val dest: Directory = destAntora.pagesDir()
                val src: Directory = when {
                    ext.antoraType.get().isComponent() -> srcAntora.pagesDir()
                    else                               -> srcAntora.getDir()
                }
                source = ss.java
                classpath = ss.compileClasspath
                destinationDirectory.set(src)
                options.annotationProcessorPath = ss.compileClasspath
                options.compilerArgs = listOf(
                    "-proc:only",
                    "-processor",
                    "io.vertx.docgen.JavaDocGenProcessor",
                    "-Adocgen.output=${dest}",
                    "-Adocgen.source=${src}/*.adoc"
                )
            }
            named<Javadoc>("javadoc") {
                onlyIf { !ext.javadocProjects.orNull.isNullOrEmpty() }
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
                    setDestinationDir(project.buildDir.resolve("docs").resolve("javadoc"))
                }
            }
            register<AntoraTask>(AntoraTask.NAME) {
                dependsOn("asciidoc", "javadoc", project.subprojects.flatMap { it.tasks.withType<AntoraTask>() })
                antoraType.set(ext.antoraType)
                antoraModule.set(ext.antoraModule)
                javadocDir.from(if (ext.javadocInDir.isEmpty) named<Javadoc>("javadoc") else javadocDir.from(ext.javadocInDir))
                outputDir.set(destAntora.getDir())
            }
            named("assemble") {
                dependsOn("antora")
            }
        }
    }
}
