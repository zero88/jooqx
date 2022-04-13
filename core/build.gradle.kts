plugins {
    `java-test-fixtures`
}

oss {
    baseName.set("jooqx")
    title.set("jOOQ.x")
}

dependencies {
    api(LogLibs.slf4j)
    api(VertxLibs.core)
    api(DatabaseLibs.jooq)
    compileOnly(VertxLibs.jdbc)

    compileOnly(VertxLibs.rx2)
    compileOnly(VertxLibs.rx3)
    compileOnly(VertxLibs.codegen)
    compileOnly(MutinyLibs.jdbc)
    compileOnly(MutinyLibs.sqlClient)
    annotationProcessor(VertxLibs.codegen)

    testImplementation(VertxLibs.sqlClient)

    testFixturesApi(project(":spi"))
    testFixturesApi(LogLibs.logback)
    testFixturesApi(TestLibs.junit5Api)
    testFixturesApi(TestLibs.junit5Engine)
    testFixturesApi(TestLibs.junit5Params)
    testFixturesApi(TestContainers.junit5)
    testFixturesApi(VertxLibs.junit5)
    testFixturesApi(ZeroLibs.utils)

    testFixturesCompileOnly(UtilLibs.jetbrainsAnnotations)
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
    register<JavaCompile>("genSrcCode") {
        genCodeByAnnotation(this, sourceSets, addToSrc = false)
        options.isFailOnError = false
        // Workaround to remove rxjava3 for Legacy SQL client due to deprecated
        // Should make this task is cacheable
        doLast {
            project.delete {
                delete(project.fileTree("${project.buildDir}/generated/main/java").matching {
                    include("**/rxjava3/Legacy*.java")
                })
            }
        }
    }

    compileJava {
        dependsOn(named("genSrcCode"))
        sourceSets.getByName("main").java.srcDirs("${project.buildDir}/generated/main/java")
    }

    register<JavaCompile>("genTestFixturesCode") {
        genCodeByAnnotation(this, sourceSets, "testFixtures")
    }

    compileTestFixturesJava {
        dependsOn(named("genTestFixturesCode"))
    }

    testFixturesJavadoc {
        title = "jOOQx Testing ${project.version} API"
    }
}
