package antora

import org.gradle.api.file.Directory
import org.gradle.api.file.DirectoryProperty

class AntoraLayout(private val module: String) {

    fun getAttachments(directory: DirectoryProperty): Directory {
        return getModule(directory).dir("attachments")
    }

    fun getPages(directory: DirectoryProperty): Directory {
        return getModule(directory).dir("pages")
    }

    private fun getModule(directory: DirectoryProperty) = directory.get().dir("modules").dir(module)
}
