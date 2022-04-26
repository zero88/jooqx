import nu.studer.gradle.jooq.JooqGenerate
import org.jooq.meta.jaxb.Logging

plugins {
    id(PluginLibs.jooq)
}

dependencies {
    jooqGenerator(JooqLibs.jooqMetaExt)
    jooqGenerator(DatabaseLibs.mysql)
    jooqGenerator(TestContainers.mysql)
    jooqGenerator(LogLibs.slf4jSimple)

    testImplementation(VertxLibs.mysql)
    testImplementation(TestContainers.mysql)
}
val dbVersion = "mysql:${(project.findProperty("dbImage") ?: "8.0-debian")}"
val sakilaSchema = "${(gradle as ExtensionAware).extensions["SAKILA_MYSQL"]}/mysql-sakila-schema.sql"
val dialect = "org.jooq.meta.mysql.MySQLDatabase"

jooq {
    version.set(JooqLibs.Version.jooq)

    configurations {
        create("testMySQLSchema") {
            jooqConfiguration.apply {
                logging = Logging.INFO
                jdbc.apply {
                    val (driver, url) = getTestContainer(dbVersion, "src/main/resources/mysql_schema.sql")
                    this.driver = driver
                    this.url = url
                }
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
                jdbc.apply {
                    val (driver, url) = getTestContainer(dbVersion, sakilaSchema, "root")
                    this.driver = driver
                    this.url = url
                }
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
    named("generateSakilaMySQLSchemaJooq") {
        enabled = false // because MySQL init script doesn't work with DELIMITER https://github.com/docker-library/mysql/issues/16
    }
}
