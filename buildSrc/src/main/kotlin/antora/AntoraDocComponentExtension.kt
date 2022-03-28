package antora

import org.gradle.api.Project
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.MapProperty
import org.gradle.api.provider.Property

abstract class AntoraDocComponentExtension {

    abstract val antoraSrcDir: Property<String>
    abstract val antoraOutDir: Property<String>
    abstract val antoraModule: Property<String>
    abstract val antoraType: Property<AntoraType>
    abstract val asciiAttributes: MapProperty<String, Any>
    abstract val docVersion: Property<String>
    abstract val javadocTitle: Property<String>
    abstract val javadocProjects: ListProperty<Project>
    abstract val javadocInDir: ConfigurableFileCollection

    init {
        antoraSrcDir.convention("src/antora")
        antoraOutDir.convention("antora")
        antoraModule.convention(AntoraCompLayout.DEFAULT_MODULE)
        antoraType.convention(AntoraType.COMPONENT)
    }

}
