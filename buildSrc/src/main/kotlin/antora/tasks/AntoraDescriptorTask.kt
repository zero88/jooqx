package antora.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.MapProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.*
import org.gradle.work.Incremental
import org.yaml.snakeyaml.Yaml
import java.io.FileWriter

@CacheableTask
abstract class AntoraDescriptorTask : DefaultTask() {

    companion object {

        const val NAME = "antoraDescriptor"
    }

    @get:Input
    @get:Optional
    abstract val docVersion: Property<String>

    @get:Input
    abstract val asciiAttributes: MapProperty<String, Any>

    @get:Incremental
    @get:PathSensitive(PathSensitivity.NAME_ONLY)
    @get:InputFile
    abstract val inputFile: RegularFileProperty

    @get:OutputFile
    abstract val outputFile: RegularFileProperty

    init {
        group = "documentation"
        outputFile.convention(inputFile)
    }

    @TaskAction
    fun generate() {
        val yaml = Yaml()
        val descriptorFile = inputFile.get().asFile
        val map = yaml.load<Map<String, Any>>(descriptorFile.inputStream()).toMutableMap()
        val docVer = project.findProperty("docVersion")?.toString()
        if (!docVer.isNullOrBlank() || map["version"].toString().ifEmpty { "null" } == "null") {
            map["version"] = docVer ?: docVersion.getOrElse(project.version.toString())
        }
        if (!asciiAttributes.orNull.isNullOrEmpty()) {
            val asciidoc = map["asciidoc"] as? Map<*, *>
            val attrs = (asciidoc?.get("attributes") as? Map<*, *>).orEmpty()
            map["asciidoc"] =
                asciidoc.orEmpty().plus(Pair("attributes", attrs.plus(asciiAttributes.get())))
        }
        yaml.dump(map, FileWriter(outputFile.get().asFile))
    }
}
