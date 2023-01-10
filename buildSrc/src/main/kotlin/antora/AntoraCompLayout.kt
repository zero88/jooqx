package antora

import org.gradle.api.file.Directory
import org.gradle.api.file.RegularFile

interface AntoraCompLayout {

    companion object {

        const val DESCRIPTOR_FILE = "antora.yml"
        const val DEFAULT_MODULE = "ROOT"

        @JvmStatic
        fun create(dir: Directory, module: String = DEFAULT_MODULE) = AntoraCompLayoutImpl(dir, module)
    }

    fun getDir(): Directory

    fun getModule(): String

    fun descriptorFile(): RegularFile = getDir().file(DESCRIPTOR_FILE)

    fun moduleDir(module: String? = null): Directory = getDir().dir("modules").dir(module ?: getModule())

    fun pagesDir(module: String? = null): Directory = moduleDir(module).dir("pages")

    fun partialsDir(module: String? = null): Directory = moduleDir(module).dir("partials")

    fun attachmentsDir(module: String? = null): Directory = moduleDir(module).dir("attachments")

}
