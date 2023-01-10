/**
 * // tag::transaction[]
 * [source,$lang,subs="attributes,verbatim"]
 * ----
 * {@link io.github.zero88.jooqx.transaction.ExampleTransaction#transaction(io.github.zero88.jooqx.Jooqx)}
 * ----
 * // end::transaction[]
 * // tag::rollbackTransaction[]
 * [source,$lang,subs="attributes,verbatim"]
 * ----
 * {@link io.github.zero88.jooqx.transaction.ExampleTransaction#rollbackTransaction(io.github.zero88.jooqx.Jooqx)}
 * ----
 * <1> First update is ok
 * <2> Second update will be failed because of table constraint
 * <3> Assert transaction exception
 * <4> You can handle transaction rollback in here
 * // end::rollbackTransaction[]
 * // tag::session[]
 * [source,$lang,subs="attributes,verbatim"]
 * ----
 * {@link io.github.zero88.jooqx.transaction.ExampleTransaction#session(io.github.zero88.jooqx.Jooqx)}
 * ----
 * // end::session[]
 * // tag::block[]
 * [source,$lang,subs="attributes,verbatim"]
 * ----
 * {@link io.github.zero88.jooqx.transaction.ExampleTransaction#block(io.github.zero88.jooqx.Jooqx)}
 * ----
 * // end::block[]
 */

@io.vertx.docgen.Document(fileName = "features-transaction.adoc")
package io.github.zero88.jooqx.transaction;
