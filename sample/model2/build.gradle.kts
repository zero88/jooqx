import nu.studer.gradle.jooq.JooqGenerate
import org.jooq.meta.jaxb.ForcedType
import org.jooq.meta.jaxb.Logging
import org.jooq.meta.jaxb.Property

plugins {
    id(PluginLibs.jooq)
}

dependencies {
    api(DatabaseLibs.jooqMetaExt)
    compileOnly(VertxLibs.pgsql)
    compileOnly(project(":spi"))

    jooqGenerator(DatabaseLibs.pgsql)
    jooqGenerator(DatabaseLibs.jooqMetaExt)
}

jooq {
    version.set(DatabaseLibs.Version.jooq)

    configurations {
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
