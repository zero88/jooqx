package antora

import org.gradle.api.file.Directory

class AntoraCompLayoutImpl(private val dir: Directory, private val module: String) : AntoraCompLayout {

    override fun getDir(): Directory = dir

    override fun getModule(): String = module

}
