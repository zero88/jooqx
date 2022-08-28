package mdtoadoc

import nl.jworks.markdown_to_asciidoc.Converter
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.*
import org.gradle.work.Incremental

@CacheableTask
abstract class MdToAdocTask : DefaultTask() {

    @get:Incremental
    @get:PathSensitive(PathSensitivity.NAME_ONLY)
    @get:InputFile
    abstract val inputFile: RegularFileProperty

    @get:OutputDirectory
    abstract val outputFolder: DirectoryProperty

    @get:Optional
    @get:OutputFile
    abstract val outputFileName: Property<String?>

    init {
        group = "documentation"
        description = "Convert .md to .adoc"
    }

    @TaskAction fun convert() {
        val input = inputFile.asFile.get()
        val outputFile = outputFolder.file(outputFileName.getOrElse(input.nameWithoutExtension + ".adoc"));
        outputFile.get().asFile.writeText(Converter.convertMarkdownToAsciiDoc(input.readText(Charsets.UTF_8)))
    }
}
