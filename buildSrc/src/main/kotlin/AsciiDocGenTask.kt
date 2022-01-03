import org.gradle.api.plugins.JavaPluginConvention
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.kotlin.dsl.the

open class AsciiDocGenTask : JavaCompile() {

    init {
        val sourceSet = project.the<JavaPluginConvention>().sourceSets.getByName(SourceSet.MAIN_SOURCE_SET_NAME)
        group = "documentation"
        source = sourceSet.java
        classpath = sourceSet.compileClasspath
        destinationDir = project.file("${project.projectDir}/src/main/resources")
        options.annotationProcessorPath = sourceSet.compileClasspath
        options.compilerArgs = listOf(
            "-proc:only",
            "-processor", "io.vertx.docgen.JavaDocGenProcessor",
            "-Adocgen.output=${project.buildDir}/asciidoc",
            "-Adocgen.source=${destinationDir}/index.adoc"
        )
    }
}
