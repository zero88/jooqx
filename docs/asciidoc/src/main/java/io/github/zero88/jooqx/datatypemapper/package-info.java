// @formatter:off
/**
 * = Data type mapper
 *
 * `Vert.x Reactive SQL client` on each Database can provide set of different object types with JDBC then `jOOQ`
 * system cannot understand. It leads to 4 use cases:
 *
 * * Both same data type. Hurray!!! No need do anything
 * * Vertx SQL data type (a.k.a. database type) is different to `jOOQ` JDBC type, then you might want one of these
 * solutions:
 * * Use `jOOQ` data type as a user type (a.k.a. your application type)
 * * Use `Vertx` data type as a user type
 * * Keep `Vertx` data type as database type, `jOOQ` is intermediate type, and your custom type in application
 *
 * So, to resolve these situations, `jooqx` introduce link:core/src/main/java/io/zero88/jooqx/datatype/DataTypeMapper
 * .java[DataTypeMapper] to make three-way-conversion that based on `jOOQ converter`. Besides that, `jooqx` provides
 * some default mapper utilities for make converter is easier with `UserTypeAsJooqType` and `UserTypeAsVertxType`.
 *
 * Let's see some example in `gradle jooq`:
 *
 * With PostgreSQL, `Interval` data type is presented by:
 *
 * * `Vert.x` PostgreSQL interval: `io.vertx.pgclient.data.Interval`
 * * `jOOQ` interval: `org.jooq.types.YearToSecond`
 *
 * [source,groovy,subs="attributes,verbatim"]
 * ----
 * generator.apply {
 *     name = "org.jooq.codegen.DefaultGenerator"
 *     strategy.name = "org.jooq.codegen.DefaultGeneratorStrategy"
 *     database.apply {
 *         name = "org.jooq.meta.postgres.PostgresDatabase"
 *         inputSchema = "public"
 *         withForcedTypes(
 *             // Use Vertx Type means io.vertx.pgclient.data.Interval is database type and user type
 *             ForcedType().withUserType("io.vertx.pgclient.data.Interval")
 *                 .withConverter("io.github.zero88.jooqx.datatype.UserTypeAsVertxType.create(new io.github.zero88
 *                 .jooqx.spi.pg.datatype.IntervalConverter())")
 *                 .withIncludeTypes("INTERVAL")
 *                 .withIncludeExpression("f_interval_1"),
 *             // Use Jooq Type means io.vertx.pgclient.data.Interval is database type, org.jooq.types.YearToSecond is jOOQ type and user type
 *             ForcedType().withUserType("io.vertx.pgclient.data.Interval")
 *                 .withConverter("io.github.zero88.jooqx.datatype.UserTypeAsJooqType.create(new io.github.zero88
 *                 .jooqx.spi.pg.datatype.IntervalConverter())")
 *                 .withIncludeTypes("INTERVAL")
 *                 .withIncludeExpression("f_interval_2"),
 *             // Three-way-conversion: io.vertx.pgclient.data.Interval is database type, org.jooq.types.YearToSecond is jOOQ intermediate type and user type is java.time.Duration
 *             ForcedType()
 *                 .withUserType("java.time.Duration")
 *                 .withConverter("io.github.zero88.jooqx.integtest.spi.pg.CustomInterval")
 *                 .withIncludeTypes("INTERVAL")
 *                 .withIncludeExpression("f_interval_3")
 *         )
 *     }
 * ----
 *
 * So, after generation, we have
 *
 * [source,java,subs="attributes,verbatim"]
 * ----
 * assert record.getFInterval1().getClass().getName() == "io.vertx.pgclient.data.Interval"
 * assert record.getFInterval2().getClass().getName() == "org.jooq.types.YearToSecond"
 * assert record.getFInterval3().getClass().getName() == "java.time.Duration"
 * ----
 *
 * [source,$lang,subs="attributes,verbatim"]
 * ----
 * {@link io.github.zero88.jooqx.datatypemapper.ExampleDataType#mapper(io.vertx.core.Vertx, io.vertx.pgclient.PgPool)}
 * ----
 */
// @formatter:on

@io.vertx.docgen.Document(fileName = "features-datatype-mapper.adoc")
package io.github.zero88.jooqx.datatypemapper;
