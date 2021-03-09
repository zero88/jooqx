plugins {
    `java-test-fixtures`
}

dependencies {
    api(LogLibs.slf4j)
    api(VertxLibs.core)
    api(DatabaseLibs.jooq)
//    api(ZeroLibs.rql_jooq)
    compileOnly(VertxLibs.sqlClient)
    compileOnly(VertxLibs.jdbc)
    compileOnly(VertxLibs.rx2)
    compileOnly(VertxLibs.codegen)
    annotationProcessor(VertxLibs.codegen)

    testFixturesApi(project(":spi"))
    testFixturesApi(LogLibs.logback)
    testFixturesApi(TestLibs.junit5Api)
    testFixturesApi(TestLibs.junit5Engine)
    testFixturesApi(TestLibs.junit5Params)
    testFixturesApi(VertxLibs.junit5)
    testFixturesApi(TestContainers.junit5)
    testFixturesApi(ZeroLibs.utils)

    testFixturesCompileOnly(UtilLibs.lombok)
    testFixturesAnnotationProcessor(UtilLibs.lombok)

    testFixturesImplementation(VertxLibs.jdbc)
    testFixturesImplementation(DatabaseLibs.h2)

    testFixturesImplementation(VertxLibs.pgsql)
    testFixturesImplementation(DatabaseLibs.pgsql)
    testFixturesImplementation(TestContainers.pgsql)

    testFixturesImplementation(VertxLibs.mysql)
    testFixturesImplementation(DatabaseLibs.mysql)
    testFixturesImplementation(TestContainers.mysql)

    testFixturesImplementation(DatabaseLibs.agroalApi)
    testFixturesImplementation(DatabaseLibs.hikari)
}

tasks.register<JavaCompile>("annotationProcessing") {
    group = "build"
    classpath = configurations.compileClasspath.get().plus(configurations.compileOnly.get())
    println(classpath.files)
    println("=================================================")

    source = sourceSets.getByName(SourceSet.MAIN_SOURCE_SET_NAME).java
    destinationDir = project.file("src/main/generated")
    options.compilerArgs = listOf(
        "-proc:only",
//        "-processor","io.vertx.codegen.CodeGenProcessor",
        "-Acodegen.output=${project.projectDir}/src/main"
    )
}
tasks.compileJava {
    dependsOn(tasks.named("annotationProcessing"))
}
sourceSets {
    main {
        java {
            srcDirs.add(project.file("src/main/generated"))
        }
    }
}
