package antora

import org.gradle.api.Project
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.ProjectLayout
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.OutputDirectory

abstract class AntoraPluginExtension(layout: ProjectLayout) {

    @get:InputDirectory
    abstract val antoraSrcDir: DirectoryProperty

    @get:OutputDirectory
    abstract val antoraOutDir: DirectoryProperty

    @get:Input
    abstract val antoraModulePage: Property<String>

    @get:Input
    abstract val javadocTitle: Property<String>

    @get:Input
    abstract val javadocProjects: ListProperty<Project>

    init {
        antoraSrcDir.convention(layout.projectDirectory.dir("src/antora"))
        antoraOutDir.convention(layout.buildDirectory.dir("antora"))
        antoraModulePage.convention("modules/ROOT/pages")
    }

}
