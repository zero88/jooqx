import cloud.playio.gradle.jooq.JooqJdbcContainer
import nu.studer.gradle.jooq.JooqGenerate
import org.jooq.meta.jaxb.ForcedType
import org.jooq.meta.jaxb.Logging

dependencies {
    compileOnly(projects.spi) // for customize generate
    compileOnly(libs.postgresVertx) // for customize generate
    jooqGenerator(libs.jooqMetaExt)
    jooqGenerator(libs.bundles.slf4jImpl)
    jooqGenerator(libs.postgresJdbc)
    jooqGenerator(libs.postgresContainer)

    testImplementation(libs.jdbcVertx)
    testImplementation(libs.bundles.postgres)
    // For pg-14
    testImplementation(libs.scram)

    testImplementation(libs.jacksonDatabind)
    testImplementation(libs.jacksonDatetime)
}
val dialect = "org.jooq.meta.postgres.PostgresDatabase"
val dbImage = DatabaseContainer.findImage(project)
fun getSchema(schemaFile: String): String = "${buildDir}/resources/main/${schemaFile}"

jooq {
    version.set(if (JavaVersion.current().majorVersion == "8") libs.versions.jooq.jdk8.get() else libs.versions.jooq.jdk17.get())
    configurations {
        create("testPgSchema") {
            jooqConfiguration.apply {
                logging = Logging.INFO
                jdbc = JooqJdbcContainer.createBySchema(dbImage, getSchema("pg_schema.sql"))
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
                jdbc = JooqJdbcContainer.createBySchema(
                    dbImage,
                    getSchema("postgres-sakila-schema.sql"),
                    mapOf("user" to "postgres")
                )
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
