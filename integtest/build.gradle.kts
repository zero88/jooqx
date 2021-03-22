import nu.studer.gradle.jooq.JooqGenerate
import org.jooq.meta.jaxb.ForcedType
import org.jooq.meta.jaxb.Logging
import org.jooq.meta.jaxb.Property

plugins {
    id(PluginLibs.jooq)
}

dependencies {
    testImplementation(project(":spi"))
    testImplementation(testFixtures(project(":core")))
    testImplementation(VertxLibs.docgen)
    testAnnotationProcessor(VertxLibs.docgen)

    jooqGenerator(DatabaseLibs.h2)
    jooqGenerator(DatabaseLibs.pgsql)
    jooqGenerator(DatabaseLibs.mysql)
    jooqGenerator(DatabaseLibs.jooqMetaExt)

    testImplementation(DatabaseLibs.agroalApi)
    testImplementation(DatabaseLibs.agroalPool)
    testImplementation(DatabaseLibs.hikari)
    testImplementation(DatabaseLibs.jooqMeta)

    testImplementation(VertxLibs.jdbc)
    testImplementation(DatabaseLibs.h2)

    testImplementation(DatabaseLibs.pgsql)
    testImplementation(VertxLibs.pgsql)
    testImplementation(TestContainers.pgsql)

    testImplementation(DatabaseLibs.mysql)
    testImplementation(VertxLibs.mysql)
    testImplementation(TestContainers.mysql)

    testImplementation(VertxLibs.rx2)
}

tasks.register<JavaCompile>("docProcessing") {
    group = "documentation"
    source = sourceSets.getByName(SourceSet.TEST_SOURCE_SET_NAME).java
    destinationDir = project.file("${projectDir}/src/asciidoc")
    classpath = configurations.testCompileClasspath.get()
    options.annotationProcessorPath = configurations.testCompileClasspath.get()
    options.compilerArgs = listOf(
        "-proc:only",
        "-processor", "io.vertx.docgen.JavaDocGenProcessor",
        "-Adocgen.output=${project.buildDir}/asciidoc",
        "-Adocgen.source=${project.projectDir}/src/asciidoc/jooqx.adoc"
    )
}

tasks.compileTestJava {
    dependsOn(tasks.named("docProcessing"))
}

sourceSets.test {
    java.srcDirs("src/test/java", "generated/test/java")
}

jooq {
    version.set(DatabaseLibs.Version.jooq)

    configurations {
        create("testH2Schema") {
            generateSchemaSourceOnCompilation.set(true)  // default (can be omitted)
            jooqConfiguration.apply {
                logging = Logging.INFO
                generator.apply {
                    name = "org.jooq.codegen.DefaultGenerator"
                    strategy.name = "org.jooq.codegen.DefaultGeneratorStrategy"
                    database.apply {
                        name = "org.jooq.meta.extensions.ddl.DDLDatabase"
                        properties.add(
                            Property().withKey("scripts").withValue("src/test/resources/h2_schema.sql")
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
                        packageName = "io.zero88.jooqx.integtest.h2"
                        directory = "build/generated/h2"
                    }
                }
            }
        }

        create("testPgSchema") {
            generateSchemaSourceOnCompilation.set(true)  // default (can be omitted)
            jooqConfiguration.apply {
                logging = Logging.INFO
                jdbc.apply {
                    driver = "org.postgresql.Driver"
                    url = "jdbc:postgresql://localhost:5423/testdb"
                    user = "postgres"
                    password = "123"
                }
                generator.apply {
                    name = "org.jooq.codegen.DefaultGenerator"
                    strategy.name = "org.jooq.codegen.DefaultGeneratorStrategy"
                    database.apply {
                        name = "org.jooq.meta.postgres.PostgresDatabase"
                        inputSchema = "public"
                        properties.add(
                            Property().withKey("scripts").withValue("src/test/resources/pg_schema.sql")
                        )
                    }
                    generate.apply {
                        isDeprecated = false
                        isRecords = true
                        isImmutablePojos = false
                        // UDT cannot generate with isInterfaces = true
                        isInterfaces = false
                        isFluentSetters = true
                        isDaos = true
                    }
                    target.apply {
                        packageName = "io.zero88.jooqx.integtest.pgsql"
                        directory = "build/generated/pgsql"
                    }
                }
            }
        }

        create("testPgSchemaWithCustomType") {
            generateSchemaSourceOnCompilation.set(true)  // default (can be omitted)
            jooqConfiguration.apply {
                logging = Logging.INFO
                jdbc.apply {
                    driver = "org.postgresql.Driver"
                    url = "jdbc:postgresql://localhost:5423/testdb"
                    user = "postgres"
                    password = "123"
                }
                generator.apply {
                    name = "org.jooq.codegen.DefaultGenerator"
                    strategy.name = "org.jooq.codegen.DefaultGeneratorStrategy"
                    database.apply {
                        name = "org.jooq.meta.postgres.PostgresDatabase"
                        inputSchema = "public"
                        properties.add(
                            Property().withKey("scripts").withValue("src/test/resources/pg_schema.sql")
                        )
                        withForcedTypes(
                            ForcedType().withUserType("io.vertx.pgclient.data.Interval")
                                .withConverter("io.zero88.jooqx.datatype.UserTypeAsVertxType.create(new io.zero88.jooqx.spi.pg.datatype.IntervalConverter())")
                                .withIncludeTypes("interval")
                                .withIncludeExpression("interval"),
                            ForcedType()
                                .withUserType("io.vertx.core.buffer.Buffer")
                                .withConverter("io.zero88.jooqx.datatype.UserTypeAsVertxType.create(new io.zero88.jooqx.datatype.basic.BytesConverter())")
                                .withIncludeTypes("bytea"),
                            ForcedType()
                                .withUserType("io.vertx.core.json.JsonObject")
                                .withConverter("io.zero88.jooqx.datatype.UserTypeAsVertxType.create(new io.zero88.jooqx.datatype.basic.JsonObjectJSONConverter())")
                                .withIncludeTypes("JSON")
                                .withIncludeExpression(".*json_data_type.JsonObject"),
                            ForcedType()
                                .withUserType("io.vertx.core.json.JsonArray")
                                .withConverter("io.zero88.jooqx.datatype.UserTypeAsVertxType.create(new io.zero88.jooqx.datatype.basic.JsonArrayJSONConverter())")
                                .withIncludeTypes("JSON")
                                .withIncludeExpression(".*json_data_type.JsonArray"),
                            ForcedType()
                                .withUserType("io.vertx.core.json.JsonObject")
                                .withConverter("io.zero88.jooqx.datatype.UserTypeAsVertxType.create(new io.zero88.jooqx.datatype.basic.JsonObjectJSONBConverter())")
                                .withIncludeTypes("JSONB")
                                .withIncludeExpression(".*jsonb_data_type.JsonObject"),
                            ForcedType()
                                .withUserType("io.vertx.core.json.JsonArray")
                                .withConverter("io.zero88.jooqx.datatype.UserTypeAsVertxType.create(new io.zero88.jooqx.datatype.basic.JsonArrayJSONBConverter())")
                                .withIncludeTypes("JSONB")
                                .withIncludeExpression(".*jsonb_data_type.JsonArray"),
                            ForcedType()
                                .withUserType("java.time.Duration")
                                .withConverter("io.zero88.jooqx.integtest.spi.pg.CustomInterval")
                                .withIncludeTypes("Interval")
                                .withIncludeExpression("f_interval")
                        )
                    }
                    generate.apply {
                        isDeprecated = false
                        isRecords = true
                        isImmutablePojos = false
                        // UDT cannot generate with isInterfaces = true
                        isInterfaces = false
                        isFluentSetters = true
                        isDaos = true
                    }
                    target.apply {
                        packageName = "io.zero88.jooqx.integtest.pgsql2"
                        directory = "build/generated/pgsql2"
                    }
                }
            }
        }

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
                            Property().withKey("scripts").withValue("src/test/resources/mysql_schema.sql")
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
                        packageName = "io.zero88.jooqx.integtest.mysql"
                        directory = "build/generated/mysql"
                    }
                }
            }
        }
    }
}

qwe {
    application.set(false)
}

project.tasks.register("generateJooq") {
    group = "jooq"
    dependsOn(tasks.withType<JooqGenerate>())
}

sourceSets {
    named(SourceSet.TEST_SOURCE_SET_NAME) {
        java.srcDirs(tasks.withType<JooqGenerate>().map { it.outputDir.get().asFile })
    }
}
