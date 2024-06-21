import cloud.playio.gradle.generator.codegen.SourceSetName
import org.gradle.util.internal.VersionNumber

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
val jooqVersion = VersionNumber.parse(
    project.configurations.compileClasspath.get()
        .resolvedConfiguration.firstLevelModuleDependencies
        .find { it.module.id.module == libs.jooq.get().module }?.moduleVersion
)
val bridges = setOf(
    DependencyBridge(
        "jooq317",
        "bridges/jooq317/java",
        jooqVersion < VersionNumber.version(3, 18),
        setOf("**/datatype/BridgeConverter.java")
    )
)

sourceSets {
    bridges.forEach {
        create(it.source) {
            java.srcDir(it.sourceDir)
            compileClasspath = sourceSets.main.get().compileClasspath
        }
    }

    main {
        java {
            bridges.filter { it.versionConstraint }.forEach { exclude(it.excludes) }
        }
    }
}


tasks {
    named<JavaCompile>("genVertxCode") {
        // Workaround to remove rxjava3 for Legacy SQL client due to deprecated
        options.isFailOnError = false
        doLast {
            project.delete {
                delete(destinationDirectory.asFileTree.matching {
                    include("**/rxjava3/Legacy*.java")
                })
            }
        }
    }

    compileJava {
        doFirst {
            bridges.filter { it.versionConstraint }.forEach {
                source = source.plus(sourceSets[it.source].java)
                exclude(sourceSets["main"].java.asFileTree.matching { exclude(it.excludes) }.asPath)
            }
        }
    }

    testFixturesJavadoc {
        title = "jOOQx Testing ${project.version} API"
    }

    withType<Jar> {
        // need to override in here, since at root project, test-fixtures is not yet recognized
        if (name == "testFixturesJar") {
            when (JavaVersion.current().majorVersion) {
                "8" -> archiveClassifier.set("test-fixtures-jvm8")
                "11" -> archiveClassifier.set("test-fixtures-jvm11")
            }
        }
    }
}
