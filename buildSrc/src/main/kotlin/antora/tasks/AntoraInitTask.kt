package antora.tasks

import antora.AntoraCompLayout
import antora.AntoraType
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.*
import org.gradle.work.Incremental

@CacheableTask
abstract class AntoraInitTask : DefaultTask() {

    companion object {

        const val NAME = "antoraInitializer"
    }

    @get:Input
    abstract val antoraType: Property<AntoraType>

    @get:Input
    abstract val antoraModule: Property<String>

    @get:Incremental
    @get:PathSensitive(PathSensitivity.NAME_ONLY)
    @get:InputDirectory
    abstract val inputDir: DirectoryProperty

    @get:OutputDirectory
    abstract val outputDir: DirectoryProperty

    init {
        group = "documentation"
        description = "Initialize Antora document component"
    }

    @TaskAction
    fun initialize() {
        val targetLayout = AntoraCompLayout.create(outputDir.get(), antoraModule.get())
        targetLayout.moduleDir().asFile.mkdirs()
        project.copy {
            from(inputDir)
            into(if (antoraType.get().isComponent()) outputDir else targetLayout.moduleDir())
        }
    }
}
