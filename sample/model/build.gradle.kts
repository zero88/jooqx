import nu.studer.gradle.jooq.JooqGenerate
import org.jooq.meta.jaxb.Logging
import org.jooq.meta.jaxb.Property

plugins {
    id(PluginLibs.jooq)
}

dependencies {
    api(DatabaseLibs.jooqMetaExt)

    compileOnly(project(":spi"))
    compileOnly(VertxLibs.pgsql)

    jooqGenerator(DatabaseLibs.h2)
    jooqGenerator(DatabaseLibs.pgsql)
    jooqGenerator(DatabaseLibs.mysql)
    jooqGenerator(DatabaseLibs.jooqMetaExt)
}

jooq {
    version.set(DatabaseLibs.Version.jooq)

    configurations {
        create("testMySQLSchema") {
            generateSchemaSourceOnCompilation.set(true)  // default (can be omitted)
            jooqConfiguration.apply {
                logging = Logging.DEBUG
                jdbc.apply {
                    driver = "com.mysql.cj.jdbc.Driver"
                    url = "jdbc:mysql://localhost:3360/testdb"
                    user = "root"
                    password = "123"
                }
                generator.apply {
                    name = "org.jooq.codegen.DefaultGenerator"
                    strategy.name = "org.jooq.codegen.DefaultGeneratorStrategy"
                    database.apply {
                        name = "org.jooq.meta.mysql.MySQLDatabase"
                        inputSchema = "testdb"
                        properties.add(
                            Property().withKey("scripts").withValue("src/main/resources/mysql_schema.sql")
                        )
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
                        packageName = "io.zero88.sample.data.mysql"
                        directory = "build/generated/mysql"
                    }
                }
            }
        }
    }
}

tasks.register("generateJooq") {
    group = "jooq"
    dependsOn(tasks.withType<JooqGenerate>())
}

sourceSets {
    main {
        java.srcDirs(tasks.withType<JooqGenerate>().map { it.outputDir.get().asFile })
    }
}
