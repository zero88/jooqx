import cloud.playio.gradle.generator.codegen.SourceSetName

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    `java-test-fixtures`
    alias(libs.plugins.codegen)
}

oss {
    baseName.set("jooqx")
    title.set("jOOQ.x")
}

codegen {
    vertx {
        version.set(libs.vertxCore.get().version)
        sources.addAll(arrayOf(SourceSetName.MAIN, SourceSetName.TEST_FIXTURES))
    }
}

dependencies {
    api(libs.vertxCore)
    api(libs.jooq)

    codeGenerator(libs.vertxRx2)
    codeGenerator(libs.vertxRx3)
    codeGenerator(libs.jdbcVertx)
    codeGenerator(libs.sqlClientVertx)
    codeGenerator(libs.mutinyCodegen)
    codeGenerator(libs.jdbcMutiny)
    codeGenerator(libs.sqlClientMutiny)

    testImplementation(libs.sqlClientVertx)

    testFixturesApi(projects.spi)
    testFixturesApi(libs.bundles.junit5)
    testFixturesApi(libs.junit5Vertx)
    testFixturesApi(libs.junit5Container)
    testFixturesApi(libs.bundles.logback)
    testFixturesApi(libs.javaUtils)

    testFixturesCompileOnly(libs.jetbrainsAnnotations)
    testFixturesCompileOnly(libs.agroalApi)
    testFixturesCompileOnly(libs.hikariCP)
    testFixturesCompileOnly(libs.jdbcVertx)
    testFixturesCompileOnly(libs.bundles.postgres)
    testFixturesCompileOnly(libs.bundles.mysql)
}

tasks {
    named<JavaCompile>("genVertxCode") {
        options.isFailOnError = false
        // Workaround to remove rxjava3 for Legacy SQL client due to deprecated
        doLast {
            project.delete {
                delete(destinationDirectory.asFileTree.matching {
                    include("**/rxjava3/Legacy*.java")
                })
            }
        }
    }

    testFixturesJavadoc {
        title = "jOOQx Testing ${project.version} API"
    }
}
