import nu.studer.gradle.jooq.JooqGenerate
import org.jooq.meta.jaxb.Logging

plugins {
    id(PluginLibs.jooq)
}

dependencies {
    jooqGenerator(DatabaseLibs.jooqMetaExt)
    jooqGenerator(DatabaseLibs.mysql)
    jooqGenerator(TestContainers.mysql)
    jooqGenerator(LogLibs.logback)

    testImplementation(VertxLibs.mysql)
    testImplementation(TestContainers.mysql)
}

fun createJdbc(jdbc: org.jooq.meta.jaxb.Jdbc, version: String) {
    val schema = "${projectDir}/src/main/resources/mysql_schema.sql"
    jdbc.driver = "org.testcontainers.jdbc.ContainerDatabaseDriver"
    jdbc.url = "jdbc:tc:mysql:${version}:///test?TC_TMPFS=/testtmpfs:rw&TC_INITSCRIPT=file:${schema}"
}

val dbImage = (project.findProperty("dbImage") ?: "8.0-debian").toString()

jooq {
    version.set(DatabaseLibs.Version.jooq)

    configurations {
        create("testMySQLSchema") {
            jooqConfiguration.apply {
                logging = Logging.INFO
                jdbc.apply { createJdbc(this, dbImage) }
                generator.apply {
                    name = "org.jooq.codegen.DefaultGenerator"
                    strategy.name = "org.jooq.codegen.DefaultGeneratorStrategy"
                    database.apply {
                        name = "org.jooq.meta.mysql.MySQLDatabase"
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
    }
}

sourceSets {
    main {
        java.srcDirs(tasks.withType<JooqGenerate>().map { it.outputDir.get().asFile })
    }
}
