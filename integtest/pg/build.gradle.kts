import nu.studer.gradle.jooq.JooqGenerate
import org.jooq.meta.jaxb.Jdbc
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

fun createJdbc(jdbc: Jdbc, version: String) {
    val schema = "${projectDir}/src/main/resources/pg_schema.sql"
    jdbc.driver = "org.testcontainers.jdbc.ContainerDatabaseDriver"
    jdbc.url = "jdbc:tc:postgresql:${version}:///hey?TC_TMPFS=/testtmpfs:rw&TC_INITSCRIPT=file:${schema}"
}

val dbImage = (project.findProperty("dbImage") ?: "10-alpine").toString()

jooq {
    version.set(JooqLibs.Version.jooq)

    configurations {
        create("testPgSchema") {
            jooqConfiguration.apply {
                logging = Logging.INFO
                jdbc.apply { createJdbc(this, dbImage) }
                generator.apply {
                    database.apply {
                        name = "org.jooq.meta.postgres.PostgresDatabase"
                        inputSchema = "public"
                        withForcedTypes(
                            org.jooq.meta.jaxb.ForcedType()
                                .withUserType("io.vertx.core.buffer.Buffer")
                                .withConverter("io.github.zero88.jooqx.datatype.UserTypeAsVertxType.create(new io.github.zero88.jooqx.datatype.basic.BytesConverter())")
                                .withIncludeTypes("bytea")
                                .withIncludeExpression("vertx_.*"),
                            org.jooq.meta.jaxb.ForcedType()
                                .withUserType("io.vertx.core.json.JsonObject")
                                .withConverter("io.github.zero88.jooqx.datatype.UserTypeAsVertxType.create(new io.github.zero88.jooqx.datatype.basic.JsonObjectJSONConverter())")
                                .withIncludeTypes("JSON")
                                .withIncludeExpression("vertx_json_data_type.JsonObject"),
                            org.jooq.meta.jaxb.ForcedType()
                                .withUserType("io.vertx.core.json.JsonArray")
                                .withConverter("io.github.zero88.jooqx.datatype.UserTypeAsVertxType.create(new io.github.zero88.jooqx.datatype.basic.JsonArrayJSONConverter())")
                                .withIncludeTypes("JSON")
                                .withIncludeExpression(".*vertx_json_data_type.JsonArray"),
                            org.jooq.meta.jaxb.ForcedType()
                                .withUserType("io.vertx.core.json.JsonObject")
                                .withConverter("io.github.zero88.jooqx.datatype.UserTypeAsVertxType.create(new io.github.zero88.jooqx.datatype.basic.JsonObjectJSONBConverter())")
                                .withIncludeTypes("JSONB")
                                .withIncludeExpression("vertx_jsonb_data_type.JsonObject"),
                            org.jooq.meta.jaxb.ForcedType()
                                .withUserType("io.vertx.core.json.JsonArray")
                                .withConverter("io.github.zero88.jooqx.datatype.UserTypeAsVertxType.create(new io.github.zero88.jooqx.datatype.basic.JsonArrayJSONBConverter())")
                                .withIncludeTypes("JSONB")
                                .withIncludeExpression("vertx_jsonb_data_type.JsonArray"),
                            org.jooq.meta.jaxb.ForcedType()
                                .withUserType("java.time.Duration")
                                .withConverter("io.github.zero88.jooqx.spi.pg.datatype.DurationConverter")
                                .withIncludeTypes("Interval")
                                .withIncludeExpression("vertx_all_data_types.f_interval"),
                            org.jooq.meta.jaxb.ForcedType()
                                .withUserType("io.vertx.pgclient.data.Interval")
                                .withConverter("io.github.zero88.jooqx.datatype.UserTypeAsVertxType.create(new io.github.zero88.jooqx.spi.pg.datatype.IntervalConverter())")
                                .withIncludeTypes("interval")
                                .withIncludeExpression("vertx_.*.interval"),
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
    }
}

sourceSets {
    main {
        java.srcDirs(tasks.withType<JooqGenerate>().map { it.outputDir.get().asFile })
    }
}
