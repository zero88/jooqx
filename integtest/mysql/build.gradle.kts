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
val dbImage = (project.findProperty("dbImage") ?: "8.0-debian").toString()

jooq {
    version.set(JooqLibs.Version.jooq)

    configurations {
        create("testMySQLSchema") {
            jooqConfiguration.apply {
                logging = Logging.INFO
                jdbc.apply {
                    val (driver, url) = getTestContainer("mysql:${dbImage}", "src/main/resources/mysql_schema.sql")
                    this.driver = driver
                    this.url = url
                }
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
