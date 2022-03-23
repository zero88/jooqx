package antora

import org.gradle.api.Project
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.ProjectLayout
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.MapProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.*
import org.gradle.work.Incremental

abstract class AntoraExtension(layout: ProjectLayout) {

    @get:Incremental
    @get:PathSensitive(PathSensitivity.NAME_ONLY)
    @get:InputDirectory
    abstract val antoraSrcDir: DirectoryProperty

    @get:OutputDirectory
    abstract val antoraOutDir: DirectoryProperty

    @get:Input
    abstract val antoraModule: Property<String>

    @get:Input
    abstract val asciiAttributes: MapProperty<String, Any>

    @get:Input
    abstract val docVersion: Property<String>

    @get:Input
    abstract val javadocTitle: Property<String>

    @get:Input
    abstract val javadocProjects: ListProperty<Project>

    @get:InputDirectory
    abstract val javadocInDir: DirectoryProperty

    @Internal
    val antoraLayout: Provider<AntoraLayout> = antoraModule.map { AntoraLayout(it) }

    init {
        antoraSrcDir.convention(layout.projectDirectory.dir("src/antora"))
        antoraOutDir.convention(layout.buildDirectory.dir("antora"))
        antoraModule.convention("ROOT")
    }

}
