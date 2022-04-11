// @formatter:off
/**
 * == Error Handler
 *
 * Basically, `exception` in execution time will be thrown by each particular `jdbc` driver or `reactive SQL driver`,
 * it can be spaghetti code, dealing with `exception`, then `jooqx` is able to centralize any exception with properly
 * `SQL state` that thanks to `DataAccessException` in `jOOQ`.
 *
 * It is easy to configure when building `executor`
 *
 * === With JDBC SQL client
 *
 * [source,$lang]
 * ----
 * {@link io.github.zero88.jooqx.errorhandler.ExampleErrorHandler#jdbcErrorHandler(io.vertx.core.Vertx, org.jooq.DSLContext, io.vertx.jdbcclient.JDBCPool)}
 * ----
 *
 * === With JDBC PostgreSQL client
 *
 * [source,$lang]
 * ----
 * {@link io.github.zero88.jooqx.errorhandler.ExampleErrorHandler#pgErrorHandler(io.vertx.core.Vertx, org.jooq.DSLContext, io.vertx.pgclient.PgPool)}
 * ----
 *
 * === Integrating with your existing application exception
 *
 * And so more, you can convert to your existing application exception (must `extends RuntimeException`) by
 *
 * [source,$lang]
 * ----
 * {@link io.github.zero88.jooqx.errorhandler.ExampleErrorHandler#integrate(io.vertx.core.Vertx, org.jooq.DSLContext, io.vertx.jdbcclient.JDBCPool)}
 * ----
 */
// @formatter:on

@io.vertx.docgen.Document(fileName = "features-error-handler.adoc")
package io.github.zero88.jooqx.errorhandler;
