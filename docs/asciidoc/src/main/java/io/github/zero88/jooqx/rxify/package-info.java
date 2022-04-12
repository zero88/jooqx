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
 * - [x] https://smallrye.io/smallrye-mutiny/[Vert.x Mutiny]
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
 *    <artifactId>vertx-rx-java2</artifactId>                     <1>
 *    <version>{vertx-version}</version>
 * </dependency>
 * <dependency>
 *    <groupId>io.vertx</groupId>
 *    <artifactId>vertx-rx-java3</artifactId>                     <2>
 *    <version>{vertx-version}</version>
 * </dependency>
 * <dependency>
 *    <groupId>io.smallrye.reactive</groupId>
 *    <artifactId>smallrye-mutiny-vertx-core</artifactId>         <3>
 *    <version>{mutiny-version}</version>
 * </dependency>
 * <dependency>
 *    <groupId>io.smallrye.reactive</groupId>
 *    <artifactId>smallrye-mutiny-vertx-pg-client</artifactId>    <4>
 *    <version>{mutiny-version}</version>
 * </dependency>
 * <dependency>
 *   <groupId>io.github.zero88</groupId>
 *   <artifactId>jooqx</artifactId>
 *   <version>{jooqx-version}</version>
 * </dependency>
 * ----
 * <1> For using `Rx2` version
 * <2> For using `Rx3` version
 * <3> For using `mutiny` version
 * <4> Assume you are using `pg-client`
 *
 * === Gradle
 *
 * In your `build.gradle` or `build.gradle.kts`
 *
 * [source,gradle,subs="attributes,verbatim"]
 * ----
 * dependencies {
 *     api("io.github.zero88:jooqx:{jooqx-version}")
 *     // with rx2
 *     api("io.vertx:vertx-rx-java2:{vertx-version}")                                 <1>
 *     // or with rx3
 *     api("io.vertx:vertx-rx-java3:{vertx-version}")                                 <2>
 *     // or with mutiny
 *     api("io.smallrye.reactive:smallrye-mutiny-vertx-core:{mutiny-version}")        <3>
 *     api("io.smallrye.reactive:smallrye-mutiny-vertx-pg-client:{mutiny-version}")   <4>
 * }
 * ----
 * <1> For using `Rx2` version
 * <2> For using `Rx3` version
 * <3> For using `mutiny` version
 * <4> Assume you are using `pg-client`
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
 *
 * === `Mutiny` - By {@code jooqx} builder
 *
 * [source,$lang,subs="attributes,verbatim"]
 * ----
 * {@link io.github.zero88.jooqx.rxify.ExampleReactivex#mutinyBuilder(io.vertx.core.Vertx, io.vertx.pgclient.PgPool, org.jooq.DSLContext)}
 * ----
 */
//@formatter:on

@io.vertx.docgen.Document(fileName = "features-rxify.adoc")
package io.github.zero88.jooqx.rxify;
