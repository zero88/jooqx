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
        version.set(VertxLibs.Version.vertxCore)
        sources.addAll(arrayOf(SourceSetName.MAIN, SourceSetName.TEST_FIXTURES))
    }
}

dependencies {
    api(VertxLibs.core)
    api(JooqLibs.jooq)

    codeGenerator(VertxLibs.jdbc)
    codeGenerator(VertxLibs.rx2)
    codeGenerator(VertxLibs.rx3)
    codeGenerator(MutinyLibs.jdbc)
    codeGenerator(MutinyLibs.sqlClient)

    testImplementation(VertxLibs.sqlClient)

    testFixturesApi(project(":spi"))
    testFixturesApi(libs.bundles.junit5)
    testFixturesApi(libs.junit5Vertx)
    testFixturesApi(libs.junit5Container)
    testFixturesApi(libs.bundles.logback)
    testFixturesApi(ZeroLibs.utils)

    testFixturesCompileOnly(libs.jetbrainsAnnotations)

    testFixturesCompileOnly(VertxLibs.rx2)

    testFixturesCompileOnly(VertxLibs.jdbc)
    testFixturesCompileOnly(libs.jdbcH2)

    testFixturesCompileOnly(VertxLibs.pgsql)
    testFixturesCompileOnly(libs.jdbcPostgres)
    testFixturesCompileOnly(libs.postgresContainer)

    testFixturesCompileOnly(VertxLibs.mysql)
    testFixturesCompileOnly(libs.jdbcMySQL)
    testFixturesCompileOnly(libs.mysqlContainer)

    testFixturesCompileOnly(libs.agroalApi)
    testFixturesCompileOnly(libs.hikariCP)
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
