dependencies {
//    api(project(":core"))
    api(VertxLibs.core)
    api(DatabaseLibs.jooq)
//    api(ZeroLibs.rql_jooq)
    compileOnly(VertxLibs.sqlClient)
    compileOnly(VertxLibs.jdbc)
    compileOnly(VertxLibs.rx2)
    compileOnly(VertxLibs.codegen)
    annotationProcessor(VertxLibs.codegen)
}

tasks.register<JavaCompile>("annotationProcessing") {
    group = "other"
    source = sourceSets.getByName(SourceSet.MAIN_SOURCE_SET_NAME).java
    destinationDir = project.file("${project.buildDir}/generated/main/java")
    classpath = configurations.compileClasspath.get()
    options.annotationProcessorPath = configurations.compileClasspath.get()
    options.compilerArgs = listOf(
        "-proc:only",
        "-processor", "io.vertx.codegen.CodeGenProcessor",
        "-Acodegen.output=${project.projectDir}/src/main"
    )
}

tasks.compileJava {
    dependsOn(tasks.named("annotationProcessing"))
}

sourceSets {
    main {
        java {
            srcDirs(project.file("${project.buildDir}/generated/main/java"))
        }
    }
}
