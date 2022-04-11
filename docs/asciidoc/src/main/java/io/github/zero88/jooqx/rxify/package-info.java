/**
 * == Rxify version
 *
 * Wanna use #jooq with #reactivex on #vertx... Stay tuned, one more step
 *
 * `jOOQ.x` supports out of the box https://vertx.io/docs/vertx-rx/java2/[Vert.x RxJava]
 *
 * To use `jOOQ.x API` for RxJava2, add the following dependency to the dependencies section of your build descriptor:
 *
 * === Maven (in your `pom.xml`)
 *
 * [source,xml]
 * ----
 * <dependency>
 *    <groupId>io.vertx</groupId>
 *    <artifactId>vertx-rx-java2</artifactId>
 *    <version>4.0.3</version>
 * </dependency>
 * <dependency>
 *   <groupId>io.github.zero88</groupId>
 *   <artifactId>jooqx</artifactId>
 *   <version>{jooqx-version}</version>
 * </dependency>
 * <!-- Other database libs depends on your application -->
 *
 * ----
 *
 * === Gradle (in your `build.gradle` or `build.gradle.kts`)
 *
 * [source,gradle]
 * ----
 * dependencies {
 *     api("io.github.zero88:jooqx:{jooqx-version}")
 *     api("io.vertx:vertx-rx-java2:4.0.3")
 *     // Other database libs depends on your application
 * }
 *
 * ----
 *
 * Then you can use same as `Vertx rx-java2 Rxified API`
 *
 * === By {@code jooqx} instance
 *
 * [source,$lang]
 * ----
 * {@link io.github.zero88.jooqx.rxify.ExampleReactivex#rx2(io.github.zero88.jooqx.Jooqx)}
 * ----
 *
 * === By reactive builder
 *
 * [source,$lang]
 * ----
 * {@link io.github.zero88.jooqx.rxify.ExampleReactivex#rx2Builder(io.vertx.reactivex.core.Vertx, org.jooq.DSLContext)}
 * ----
 */

@io.vertx.docgen.Document(fileName = "features-rxify.adoc")
package io.github.zero88.jooqx.rxify;
