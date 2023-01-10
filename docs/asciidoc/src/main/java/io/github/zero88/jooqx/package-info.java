/**
 * [source,$lang,subs="attributes,verbatim"]
 * ----
 * {@link io.github.zero88.jooqx.JooqxBasic#future(io.vertx.core.Vertx)}
 * ----
 * <1> Create PostgreSQL connection options
 * <2> Create Pool Options
 * <3> Create PgPool
 * <4> Init jOOQ DSL context
 * <5> Create `jooqx` instance
 * <6> Execute jOOQ query
 * <7> Handle async query result
 * <8> Access record object seamlessly without cast type
 * <9> Handle async error
 */

@io.vertx.docgen.Document(fileName = "features-basic.adoc")
package io.github.zero88.jooqx;
