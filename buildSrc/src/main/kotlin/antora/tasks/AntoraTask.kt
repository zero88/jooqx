package antora.tasks

import antora.AntoraCompLayout
import antora.AntoraType
import org.gradle.api.DefaultTask
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.*
import org.gradle.kotlin.dsl.withType
import org.gradle.work.Incremental

@CacheableTask
abstract class AntoraTask : DefaultTask() {
    companion object {

        const val NAME = "antora"
    }

    @get:Incremental
    @get:PathSensitive(PathSensitivity.NAME_ONLY)
    @get:InputFiles
    @get:Optional
    abstract val javadocDir: ConfigurableFileCollection

    @get:Input
    abstract val antoraType: Property<AntoraType>

    @get:Input
    abstract val antoraModule: Property<String>

    @get:OutputDirectory
    abstract val outputDir: DirectoryProperty

    init {
        group = "documentation"
        description = "Finalize Antora document component"
    }

    @TaskAction
    fun finalize() {
        if (antoraType.get().isComponent()) {
            project.copy {
                from(project.subprojects.flatMap { it.tasks.withType<AntoraTask>() })
                into(outputDir)
            }
        }
        project.copy {
            from(javadocDir)
            into(AntoraCompLayout.create(outputDir.get(), antoraModule.get()).attachmentsDir().dir("javadoc"))
        }
    }
}
