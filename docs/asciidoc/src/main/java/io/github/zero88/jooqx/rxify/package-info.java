//@formatter:off
/**
 * = Rxify version
 *
 * Wanna use #jooq with #reactivex on #vertx... Stay tuned, one more step
 *
 * `jOOQ.x` supports out of the box almost reactive version that officially supports by https://vertx.io/docs/#reactive[Vert.x]
 *
 * - [x] https://vertx.io/docs/vertx-rx/java2/[Vert.x RxJava2]
 * - [x] https://vertx.io/docs/vertx-rx/java3/[Vert.x RxJava3]
 *
 * == Dependencies setup
 *
 * To use `jOOQ.x API` with reactive version, add the following dependency to the dependencies section of your build descriptor:
 *
 * === Maven
 *
 * In your `pom.xml`
 *
 * [source,xml,subs="attributes,verbatim"]
 * ----
 * <dependency>
 *    <groupId>io.vertx</groupId>
 *    <artifactId>vertx-rx-java2</artifactId>  <1>
 *    <version>{vertx-version}</version>
 * </dependency>
 * <dependency>
 *    <groupId>io.vertx</groupId>
 *    <artifactId>vertx-rx-java3</artifactId>  <2>
 *    <version>{vertx-version}</version>
 * </dependency>
 * <dependency>
 *   <groupId>io.github.zero88</groupId>
 *   <artifactId>jooqx</artifactId>
 *   <version>{jooqx-version}</version>
 * </dependency>
 * ----
 * <1> For using `Rx2` version
 * <2> For using `Rx3` version
 *
 * === Gradle
 *
 * In your `build.gradle` or `build.gradle.kts`
 *
 * [source,gradle,subs="attributes,verbatim"]
 * ----
 * dependencies {
 *     api("io.github.zero88:jooqx:{jooqx-version}")    <1>
 *     // with rx2
 *     api("io.vertx:vertx-rx-java2:{vertx-version}")   <2>
 *     // or with rx3
 *     api("io.vertx:vertx-rx-java3:{vertx-version}")
 *     // Other database libs depends on your application
 * }
 * ----
 * <1> For using `Rx2` version
 * <2> For using `Rx3` version
 *
 * Well done, now you can enjoy Rxify version with a little effort
 *
 * == Examples
 *
 * === `Rx2` - By {@code jooqx} instance
 *
 * [source,$lang,subs="attributes,verbatim"]
 * ----
 * {@link io.github.zero88.jooqx.rxify.ExampleReactivex#rx2(io.github.zero88.jooqx.Jooqx)}
 * ----
 *
 * === `Rx2` - By {@code jooqx} builder
 *
 * [source,$lang,subs="attributes,verbatim"]
 * ----
 * {@link io.github.zero88.jooqx.rxify.ExampleReactivex#rx2Builder(io.vertx.core.Vertx, io.vertx.jdbcclient.JDBCPool, org.jooq.DSLContext)}
 * ----
 *
 * === `Rx3` - By {@code jooqx} builder
 *
 * [source,$lang,subs="attributes,verbatim"]
 * ----
 * {@link io.github.zero88.jooqx.rxify.ExampleReactivex#rx3Builder(io.vertx.core.Vertx, io.vertx.mysqlclient.MySQLPool, org.jooq.DSLContext)}
 * ----
 */
//@formatter:on

@io.vertx.docgen.Document(fileName = "features-rxify.adoc")
package io.github.zero88.jooqx.rxify;
