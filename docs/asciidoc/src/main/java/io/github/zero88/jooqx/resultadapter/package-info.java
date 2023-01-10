/**
 * // tag::jsonRecord[]
 * [source,$lang,subs="attributes,verbatim"]
 * ----
 * {@link io.github.zero88.jooqx.resultadapter.ExampleResultAdapter#toJsonRecord(io.github.zero88.jooqx.Jooqx)}
 * ----
 * <1> Execute query
 * <2> Access Json record object seamlessly without cast type
 * <3> output: `{"id":88,"name":"zero88","country":"VN"}`
 * // end::jsonRecord[]
 * // tag::byClass[]
 * [source,$lang,subs="attributes,verbatim"]
 * ----
 * {@link io.github.zero88.jooqx.resultadapter.ExampleResultAdapter#toClass(io.github.zero88.jooqx.Jooqx)}
 * ----
 * // end::byClass[]
 * // tag::byFields[]
 * [source,$lang,subs="attributes,verbatim"]
 * ----
 * {@link io.github.zero88.jooqx.resultadapter.ExampleResultAdapter#toFields(io.github.zero88.jooqx.Jooqx)}
 * ----
 * // end::byFields[]
 */

@io.vertx.docgen.Document(fileName = "features-result-adapter.adoc")
package io.github.zero88.jooqx.resultadapter;
