/**
 * == Basic usage
 *
 * === Simple execution
 * [source,$lang]
 * ----
 * {@link io.github.zero88.jooqx.JooqxBasic#future(io.vertx.core.Vertx)}
 * ----
 * <1> Create PostgreSQL connection options
 * <2> Create Pool Options
 * <3> Create PgPool
 * <4> Init jOOQ DSL context
 * <5> Create `jooqx` instance
 * <6> Build jOOQ query
 * <7> Execute query
 * <8> Handle async query result
 * <9> Access record object seamlessly without cast type
 * <10> Handle async error
 */

@io.vertx.docgen.Document(fileName = "features-basic.adoc")
package io.github.zero88.jooqx;
