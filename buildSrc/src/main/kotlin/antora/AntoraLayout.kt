package antora

import org.gradle.api.file.Directory
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFile

class AntoraLayout(private val module: String) {

    companion object {

        @JvmStatic
        fun getDescriptor(directory: DirectoryProperty): RegularFile {
            return arrayOf("antora.yml", "antora.yaml")
                .mapNotNull { directory.get().file(it).takeIf { rf -> rf.asFile.isFile } }.firstOrNull()
                ?: throw IllegalArgumentException("Not found antora.yml")
        }

    }

    fun getAttachments(directory: DirectoryProperty): Directory {
        return getModule(directory).dir("attachments")
    }

    fun getPages(directory: DirectoryProperty): Directory {
        return getModule(directory).dir("pages")
    }

    private fun getModule(directory: DirectoryProperty) = directory.get().dir("modules").dir(module)
}
