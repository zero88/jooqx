// @formatter:off
/**
 * // tag::jdbcErrorHandler[]
 * [source,$lang,subs="attributes,verbatim"]
 * ----
 * {@link io.github.zero88.jooqx.errorhandler.ExampleErrorHandler#jdbcErrorHandler(io.vertx.core.Vertx, org.jooq.DSLContext, io.vertx.jdbcclient.JDBCPool)}
 * ----
 * // end::jdbcErrorHandler[]
 * // tag::pgErrorHandler[]
 * [source,$lang,subs="attributes,verbatim"]
 * ----
 * {@link io.github.zero88.jooqx.errorhandler.ExampleErrorHandler#pgErrorHandler(io.vertx.core.Vertx, org.jooq.DSLContext, io.vertx.pgclient.PgPool)}
 * ----
 * // end::pgErrorHandler[]
 * // tag::integrateErrorHandler[]
 * [source,$lang,subs="attributes,verbatim"]
 * ----
 * {@link io.github.zero88.jooqx.errorhandler.ExampleErrorHandler#integrate(io.vertx.core.Vertx, org.jooq.DSLContext, io.vertx.jdbcclient.JDBCPool)}
 * ----
 * // end::integrateErrorHandler[]
 */
// @formatter:on

@io.vertx.docgen.Document(fileName = "features-error-handler.adoc")
package io.github.zero88.jooqx.errorhandler;
