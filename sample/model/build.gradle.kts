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
                            Property().withKey("scripts").withValue("src/main/resources/h2_schema.sql")
                        )
                    }
                    generate.apply {
                        isRecords = true
                        isFluentSetters = true
                        isDeprecated = false
                        isImmutablePojos = false
                        isInterfaces = false
                        isDaos = false
                    }
                    target.apply {
                        packageName = "io.zero88.sample.data.h2"
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
                            Property().withKey("scripts").withValue("src/main/resources/pg_schema.sql")
                        )
                    }
                    generate.apply {
                        isRecords = true
                        isFluentSetters = true
                        isPojos = true
                        isDeprecated = false
                        isImmutablePojos = false
                        // UDT cannot generate with isInterfaces = true
                        isInterfaces = false
                        isDaos = false
                    }
                    target.apply {
                        packageName = "io.zero88.sample.data.pgsql"
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
                            Property().withKey("scripts").withValue("../model/src/main/resources/pg_schema.sql")
                        )
                        withForcedTypes(
                            org.jooq.meta.jaxb.ForcedType().withUserType("io.vertx.pgclient.data.Interval")
                                .withConverter("io.zero88.jooqx.datatype.UserTypeAsVertxType.create(new io.zero88.jooqx.spi.pg.datatype.IntervalConverter())")
                                .withIncludeTypes("interval")
                                .withIncludeExpression("interval"),
                            org.jooq.meta.jaxb.ForcedType()
                                .withUserType("io.vertx.core.buffer.Buffer")
                                .withConverter("io.zero88.jooqx.datatype.UserTypeAsVertxType.create(new io.zero88.jooqx.datatype.basic.BytesConverter())")
                                .withIncludeTypes("bytea"),
                            org.jooq.meta.jaxb.ForcedType()
                                .withUserType("io.vertx.core.json.JsonObject")
                                .withConverter("io.zero88.jooqx.datatype.UserTypeAsVertxType.create(new io.zero88.jooqx.datatype.basic.JsonObjectJSONConverter())")
                                .withIncludeTypes("JSON")
                                .withIncludeExpression(".*json_data_type.JsonObject"),
                            org.jooq.meta.jaxb.ForcedType()
                                .withUserType("io.vertx.core.json.JsonArray")
                                .withConverter("io.zero88.jooqx.datatype.UserTypeAsVertxType.create(new io.zero88.jooqx.datatype.basic.JsonArrayJSONConverter())")
                                .withIncludeTypes("JSON")
                                .withIncludeExpression(".*json_data_type.JsonArray"),
                            org.jooq.meta.jaxb.ForcedType()
                                .withUserType("io.vertx.core.json.JsonObject")
                                .withConverter("io.zero88.jooqx.datatype.UserTypeAsVertxType.create(new io.zero88.jooqx.datatype.basic.JsonObjectJSONBConverter())")
                                .withIncludeTypes("JSONB")
                                .withIncludeExpression(".*jsonb_data_type.JsonObject"),
                            org.jooq.meta.jaxb.ForcedType()
                                .withUserType("io.vertx.core.json.JsonArray")
                                .withConverter("io.zero88.jooqx.datatype.UserTypeAsVertxType.create(new io.zero88.jooqx.datatype.basic.JsonArrayJSONBConverter())")
                                .withIncludeTypes("JSONB")
                                .withIncludeExpression(".*jsonb_data_type.JsonArray"),
                            org.jooq.meta.jaxb.ForcedType()
                                .withUserType("java.time.Duration")
                                .withConverter("io.zero88.jooqx.spi.pg.datatype.DurationConverter")
                                .withIncludeTypes("Interval")
                                .withIncludeExpression("f_interval")
                        )
                    }
                    generate.apply {
                        isRecords = true
                        isFluentSetters = true
                        isDeprecated = false
                        isImmutablePojos = false
                        // UDT cannot generate with isInterfaces = true
                        isInterfaces = false
                        isDaos = false
                    }
                    target.apply {
                        packageName = "io.zero88.sample.data.pgsql2"
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
