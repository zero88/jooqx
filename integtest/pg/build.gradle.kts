import nu.studer.gradle.jooq.JooqGenerate
import org.jooq.meta.jaxb.ForcedType
import org.jooq.meta.jaxb.Logging

dependencies {
    compileOnly(project(":spi")) // for customize generate
    compileOnly(VertxLibs.pgsql) // for customize generate
    jooqGenerator(LogLibs.slf4jSimple)
    jooqGenerator(JooqLibs.jooqMetaExt)
    jooqGenerator(DatabaseLibs.pgsql)
    jooqGenerator(TestContainers.pgsql)

    testImplementation(VertxLibs.jdbc)
    testImplementation(VertxLibs.pgsql)
    testImplementation(DatabaseLibs.pgsql)
    testImplementation(TestContainers.pgsql)
}
val dbVersion = "postgresql:${(project.findProperty("dbImage") ?: "10-alpine")}"
val dialect = "org.jooq.meta.postgres.PostgresDatabase"
fun getSchema(schemaFile: String): String = "${buildDir}/resources/main/${schemaFile}"

jooq {
    version.set(JooqLibs.Version.jooq)

    configurations {
        create("testPgSchema") {
            jooqConfiguration.apply {
                logging = Logging.INFO
                jdbc.apply {
                    val (driver, url) = getTestContainer(dbVersion, getSchema("pg_schema.sql"))
                    this.driver = driver
                    this.url = url
                }
                generator.apply {
                    database.apply {
                        name = dialect
                        inputSchema = "public"
                        withForcedTypes(
                            ForcedType()
                                .withUserType("io.vertx.core.buffer.Buffer")
                                .withConverter("io.github.zero88.jooqx.datatype.UserTypeAsVertxType.create(new io.github.zero88.jooqx.datatype.basic.BytesConverter())")
                                .withIncludeTypes("bytea")
                                .withIncludeExpression("vertx_.*"),
                            ForcedType()
                                .withUserType("io.vertx.core.json.JsonObject")
                                .withConverter("io.github.zero88.jooqx.datatype.UserTypeAsVertxType.create(new io.github.zero88.jooqx.datatype.basic.JsonObjectJSONConverter())")
                                .withIncludeTypes("JSON")
                                .withIncludeExpression("vertx_.*.json_object"),
                            ForcedType()
                                .withUserType("io.vertx.core.json.JsonArray")
                                .withConverter("io.github.zero88.jooqx.datatype.UserTypeAsVertxType.create(new io.github.zero88.jooqx.datatype.basic.JsonArrayJSONConverter())")
                                .withIncludeTypes("JSON")
                                .withIncludeExpression("vertx_.*.json_array"),
                            ForcedType()
                                .withUserType("io.vertx.core.json.JsonObject")
                                .withConverter("io.github.zero88.jooqx.datatype.UserTypeAsVertxType.create(new io.github.zero88.jooqx.datatype.basic.JsonObjectJSONBConverter())")
                                .withIncludeTypes("JSONB")
                                .withIncludeExpression("vertx_.*.jsonb_object"),
                            ForcedType()
                                .withUserType("io.vertx.core.json.JsonArray")
                                .withConverter("io.github.zero88.jooqx.datatype.UserTypeAsVertxType.create(new io.github.zero88.jooqx.datatype.basic.JsonArrayJSONBConverter())")
                                .withIncludeTypes("JSONB")
                                .withIncludeExpression("vertx_.*.jsonb_array"),
                            ForcedType()
                                .withUserType("io.vertx.pgclient.data.Interval")
                                .withConverter("io.github.zero88.jooqx.datatype.UserTypeAsVertxType.create(new io.github.zero88.jooqx.spi.pg.datatype.IntervalConverter())")
                                .withIncludeTypes("interval")
                                .withIncludeExpression("vertx_.*.interval"),
                            ForcedType()
                                .withUserType("java.time.Duration")
                                .withConverter("io.github.zero88.jooqx.spi.pg.datatype.DurationConverter")
                                .withIncludeTypes("Interval")
                                .withIncludeExpression("vertx_.*.duration"),
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
                        packageName = "io.github.zero88.sample.model.pgsql"
                        directory = "build/generated/pgsql"
                    }
                }
            }
        }

        create("sakilaPgSchema") {
            jooqConfiguration.apply {
                logging = Logging.INFO
                jdbc.apply {
                    val (driver, url) = getTestContainer(dbVersion, getSchema("postgres-sakila-schema.sql"), mapOf("user" to "postgres"))
                    this.driver = driver
                    this.url = url
                }
                generator.apply {
                    database.apply {
                        name = dialect
                        inputSchema = "public"
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
                        packageName = "io.github.zero88.sample.model.sakila.pgsql"
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
                from((gradle as ExtensionAware).extensions["SAKILA_PG"])
                into(destinationDir)
            }
        }
    }
    withType<JooqGenerate> {
        dependsOn("processResources")
    }
}
