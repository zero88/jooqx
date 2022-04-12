/**
 * = Result adapter
 *
 * == JsonRecord
 *
 * Vertx JsonObject vs jOOQ Record... Ya, merging: `JsonRecord`
 *
 * [source,$lang,subs="attributes,verbatim"]
 * ----
 * {@link io.github.zero88.jooqx.resultadapter.ExampleResultAdapter#toJsonRecord(io.github.zero88.jooqx.Jooqx)}
 * ----
 * <1> Execute query
 * <2> Access Json record object seamlessly without cast type
 * <3> output: `{"id":88,"name":"zero88","country":"VN"}`
 *
 * == By Table
 *
 * [source,$lang,subs="attributes,verbatim"]
 * ----
 * {@link io.github.zero88.jooqx.resultadapter.ExampleResultAdapter#toTableRecord(io.github.zero88.jooqx.Jooqx)}
 * ----
 *
 * == By Fields
 *
 * [source,$lang,subs="attributes,verbatim"]
 * ----
 * {@link io.github.zero88.jooqx.resultadapter.ExampleResultAdapter#toFields(io.github.zero88.jooqx.Jooqx)}
 * ----
 *
 */

@io.vertx.docgen.Document(fileName = "features-result-adapter.adoc")
package io.github.zero88.jooqx.resultadapter;
