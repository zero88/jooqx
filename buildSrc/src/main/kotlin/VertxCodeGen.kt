import org.gradle.api.tasks.SourceSetContainer
import org.gradle.api.tasks.compile.JavaCompile

fun genCodeByAnnotation(jc: JavaCompile, sourceSets: SourceSetContainer, sourceSet: String = "main", addToSrc: Boolean = true) {
    jc.apply {
        group = "other"
        source = sourceSets.getByName(sourceSet).java
        destinationDirectory.set(project.file("${project.buildDir}/generated/${sourceSet}/java"))
        classpath = sourceSets.getByName(sourceSet).compileClasspath
        options.annotationProcessorPath = jc.classpath
        options.compilerArgs = listOf(
            "-proc:only",
            "-processor", "io.vertx.codegen.CodeGenProcessor",
            "-Acodegen.output=${project.projectDir}/src/${sourceSet}"
        )
    }
    if (addToSrc) {
        sourceSets.getByName(sourceSet).java.srcDirs(jc.destinationDirectory.get())
    }
}
