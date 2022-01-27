import org.gradle.api.tasks.SourceSetContainer
import org.gradle.api.tasks.compile.JavaCompile

fun genCodeByAnnotation(jc: JavaCompile, sourceSets: SourceSetContainer, sourceSet: String = "main") {
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
    sourceSets.getByName(sourceSet).java.srcDirs(jc.destinationDirectory.get())
}
