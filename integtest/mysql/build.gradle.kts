import cloud.playio.gradle.jooq.JooqJdbcContainer
import cloud.playio.gradle.jooq.loadDbSchema
import nu.studer.gradle.jooq.JooqGenerate
import org.jooq.meta.jaxb.Logging

plugins {
    id(PluginLibs.jooq)
}

dependencies {
    jooqGenerator(JooqLibs.jooqMetaExt)
    jooqGenerator(DatabaseLibs.mysql)
    jooqGenerator(TestLibs.mysqlTestContainer)
    jooqGenerator(LogLibs.slf4jSimple)
    jooqGenerator(testFixtures(project(":jooqx")))

    testImplementation(VertxLibs.mysql)
    testImplementation(TestLibs.mysqlTestContainer)
}
val dialect = "org.jooq.meta.mysql.MySQLDatabase"
val dbImage = "mysql:${(project.findProperty("dbVersion") ?: DatabaseContainer.mysql[0])}"
fun getSchema(schemaFile: String): String = "${buildDir}/resources/main/${schemaFile}"

jooq {
    version.set(JooqLibs.Version.jooq)

    configurations {
        create("testMySQLSchema") {
            jooqConfiguration.apply {
                logging = Logging.INFO
                jdbc = JooqJdbcContainer.createByFunction(
                    dbImage,
                    "io.github.zero88.jooqx.spi.mysql.MySQLInitializer::init",
                    mapOf("user" to "root", "allowMultiQueries" to true)
                )
                generator.apply {
                    database.apply {
                        name = dialect
                        inputSchema = "test"
                    }
                    generate.apply {
                        isDeprecated = false
                        isRecords = true
                        isImmutablePojos = false
                        isInterfaces = true
                        isFluentSetters = true
                        isDaos = true
                    }
                    target.apply {
                        packageName = "io.github.zero88.sample.model.mysql"
                        directory = "build/generated/mysql"
                    }
                }
            }
        }

        create("sakilaMySQLSchema") {
            jooqConfiguration.apply {
                logging = Logging.INFO
                jdbc = JooqJdbcContainer.createByFunction(
                    dbImage,
                    "io.github.zero88.jooqx.spi.mysql.MySQLInitializer::init",
                    mapOf("user" to "root", "allowMultiQueries" to true)
                )
                generator.apply {
                    database.apply {
                        name = dialect
                        inputSchema = "test"
                    }
                    generate.apply {
                        isDeprecated = false
                        isRecords = true
                        isImmutablePojos = false
                        isInterfaces = true
                        isFluentSetters = true
                        isDaos = true
                    }
                    target.apply {
                        packageName = "io.github.zero88.sample.model.sakila.mysql"
                        directory = "build/generated/sakila"
                    }
                }
            }
        }
    }
}

sourceSets {
    main {
        java.srcDirs(tasks.withType<JooqGenerate>().map { it.outputDir.get().asFile })
    }
}

tasks {
    processResources {
        doLast {
            copy {
                from((gradle as ExtensionAware).extensions["SAKILA_MYSQL"])
                into(destinationDir)
            }
        }
    }
    withType<JooqGenerate> {
        dependsOn("processResources")
    }
    named<JooqGenerate>("generateTestMySQLSchemaJooq").configure {
        setJavaExecSpec { loadDbSchema(project, getSchema("mysql_schema.sql")) }
    }
    named<JooqGenerate>("generateSakilaMySQLSchemaJooq").configure {
        setJavaExecSpec { loadDbSchema(project, getSchema("mysql-sakila-schema.sql")) }
        // because MySQL init script doesn't work with DELIMITER https://github.com/docker-library/mysql/issues/16
        enabled = false
    }
}
