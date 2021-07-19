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

    testImplementation(VertxLibs.sqlClient)

    testFixturesApi(project(":spi"))
    testFixturesApi(project(":provider"))
    testFixturesApi(LogLibs.logback)
    testFixturesApi(TestLibs.junit5Api)
    testFixturesApi(TestLibs.junit5Engine)
    testFixturesApi(TestLibs.junit5Params)
    testFixturesApi(VertxLibs.junit5)
    testFixturesApi(TestContainers.junit5)
    testFixturesApi(ZeroLibs.utils)

    testFixturesCompileOnly(UtilLibs.lombok)
    testFixturesAnnotationProcessor(UtilLibs.lombok)
    testFixturesCompileOnly(VertxLibs.codegen)
    testFixturesAnnotationProcessor(VertxLibs.codegen)

    testFixturesImplementation(VertxLibs.rx2)

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

tasks {
    register<JavaCompile>("annotationProcessing") {
        doAnnotationProcessing(this, sourceSets, SourceSet.MAIN_SOURCE_SET_NAME, configurations.compileClasspath)
    }

    register<JavaCompile>("testFixturesAnnotationProcessing") {
        doAnnotationProcessing(this, sourceSets, "testFixtures", configurations.testFixturesCompileClasspath)
    }

    compileJava {
        dependsOn(named("annotationProcessing"))
    }

    compileTestFixturesJava {
        dependsOn(named("testFixturesAnnotationProcessing"))
    }
}

sourceSets {
    main {
        java {
            srcDirs(project.file("${project.buildDir}/generated/main/java"))
        }
    }
    testFixtures {
        java {
            srcDirs(project.file("${project.buildDir}/generated/testFixtures/java"))
        }
    }
}


fun doAnnotationProcessing(
    javaCompile: JavaCompile,
    sourceSets: SourceSetContainer,
    sourceSet: String,
    cp: NamedDomainObjectProvider<Configuration>
) {
    javaCompile.group = "other"
    javaCompile.source = sourceSets.getByName(sourceSet).java
    javaCompile.destinationDir = javaCompile.project.file("${javaCompile.project.buildDir}/generated/${sourceSet}/java")
    javaCompile.classpath = cp.get()
    javaCompile.options.annotationProcessorPath = cp.get()
    javaCompile.options.compilerArgs = listOf(
        "-proc:only",
        "-processor", "io.vertx.codegen.CodeGenProcessor",
        "-Acodegen.output=${javaCompile.project.projectDir}/src/${sourceSet}"
    )
}
