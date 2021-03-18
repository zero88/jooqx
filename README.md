# jOOQ.x - Vertx jOOQ DSL

![build](https://github.com/zero88/vertx-jooq-dsl/workflows/build-release/badge.svg?branch=main)
![GitHub release (latest SemVer)](https://img.shields.io/github/v/release/zero88/jooqx?sort=semver)
![Sonatype Nexus (Releases)](https://img.shields.io/nexus/r/io.github.zero88/jooqx-core?server=https%3A%2F%2Foss.sonatype.org)
![Sonatype Nexus (Snapshots)](https://img.shields.io/nexus/s/io.github.zero88/jooqx-core?server=https%3A%2F%2Foss.sonatype.org)
[![Lines of Code](https://sonarcloud.io/api/project_badges/measure?project=zero88_jooqx&metric=ncloc)](https://sonarcloud.io/dashboard?id=zero88_jooqx)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=zero88_jooqx&metric=coverage)](https://sonarcloud.io/dashboard?id=zero88_jooqx)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=zero88_jooqx&metric=sqale_rating)](https://sonarcloud.io/dashboard?id=zero88_jooqx)
[![Reliability Rating](https://sonarcloud.io/api/project_badges/measure?project=zero88_jooqx&metric=reliability_rating)](https://sonarcloud.io/dashboard?id=zero88_jooqx)
[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=zero88_jooqx&metric=security_rating)](https://sonarcloud.io/dashboard?id=zero88_jooqx)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=zero88_jooqx&metric=alert_status)](https://sonarcloud.io/dashboard?id=zero88_jooqx)

`jooqx` leverages the power of typesafe SQL from [jOOQ DSL](https://www.jooq.org) and running on SQL connection in a reactive and non-blocking of `SQL driver` from [Vert.x](https://vertx.io/docs/#databases)

## Features

`jooqx` Provide uniform API for among

- [x] Vertx [Legacy SQL client](https://vertx.io/docs/vertx-jdbc-client/java/#_legacy_jdbc_client_api)
- [x] Vertx [Reactive SQL client](https://github.com/eclipse-vertx/vertx-sql-client)
- [ ] Vertx platform and Plain `JDBC`

`jooqx` supports:

- [x] Lightweight and SPI
- [x] Typesafe
- [x] Native `jOOQ DSL` API
- [x] Any database with `JDBC` driver and combine with legacy SQL
- [x] Any database with `Vert.x reactive client`(`JDBCPool` is `reactive JDBC driver` so almost databases should work properly)
- [x] JsonObject record
- [x] Row/record transformation to `jOOQ` record
- [x] A unified format for `exception` and able to replace/integrate seamlessly by your current application exception
- [x] `CRUD` with prepared query by `jOOQ DSL`
- [x] SQL Complex query with typesafe such as `join`, `with`, `having to`, etc
- [x] SQL Batch insert/update/merge
- [x] SQL Transaction
    - [?] Nested transaction (not yet tested, but API is available)
    - [ ] Transaction rollback condition
    - [ ] Transaction annotation
- [ ] Procedure
- [ ] DAO
- [?] Resource Query Language (RQL) from [rsql-jooq](https://github.com/zero88/rsql)
- [x] Rxified API
- [ ] Row streaming
- [ ] Publish/subscribe
- [x] Test fixtures API to easy setup test for your application testing or produce a minimum reproducer

## Usage

To use `jooqx` add the following [dependency](https://search.maven.org/artifact/io.github.zero88/jooqx-core/1.0.0/jar) to the dependencies section of your build descriptor:

- `Maven` (in your `pom.xml`):

```xml
<dependency>
    <groupId>io.github.zero88</groupId>
    <artifactId>jooqx-core</artifactId>
    <version>1.0.0</version>
</dependency>
<!-- For some SPI includes specific converter based on Vert.x database client -->
<dependency>
  <groupId>io.github.zero88</groupId>
  <artifactId>jooqx-spi</artifactId>
  <version>1.0.0</version>
</dependency>
```

- `Gradle` (in your `build.gradle`):

```groovy
dependencies {
    api("io.github.zero88:jooqx-core:1.0.0")
    // For some SPI includes specific converter based on Vert.x database client
    api("io.github.zero88:jooqx-spi:1.0.0")
}
```

**Hint**

`jooqx` is only depended on 3 main libraries:

- `io.vertx:vertx-core`
- `org.jooq:jooq`
- `org.slf4j:slf4j-api`

Adding this `jooqx` library JAR will not automatically add a `database driver` JAR to your project. You should
ensure that your project also has a suitable `database driver` as a dependency.

For example:

- With `legacy JDBC` and connecting to `MySQL` driver

```groovy
dependencies {
    api("mysql:mysql-connector-java:8.0.23")
    // It is recommendation to use HikariCP instead of c3p0
    api("com.zaxxer:HikariCP:4.0.2")
    api("io.vertx:vertx-jdbc-client:4.0.2") {
        exclude("com.mchange")
    }
    api("io.github.zero88:jooqx-core:1.0.0")
}
```

- With `reactive PostgreSQL` client

```groovy
dependencies {
    api("io.vertx:vertx-pg-client:4.0.2")
    api("io.github.zero88:jooqx-core:1.0.0")
}
```

- With `reactive JDBC` client and `H2`

```groovy
dependencies {
    api("com.h2database:h2:1.4.200")
    // Agroal pool - Default in Vertx SQL client - Not yet has alternatives
    api("io.agroal:agroal-pool:1.9")
    api("io.vertx:vertx-sql-client:4.0.2")
    api("io.github.zero88:jooqx-core:1.0.0")
}
```

## Getting started

Assume you know how to use [jOOQ code generation](https://www.jooq.org/doc/3.14/manual/code-generation/) and able to
generate your database schema.

You can use: [Maven jOOQ codegen](https://www.jooq.org/doc/3.14/manual/code-generation/codegen-maven/)
or [Gradle jOOQ plugin](https://github.com/etiennestuder/gradle-jooq-plugin)

Better experimental is checkout my [integtest](./integtest) project to see some examples

### Simple query

- Legacy SQL client

```java
// Init JDBCClient legacy client
SQLClient client = JDBCClient.create(vertx, config);
DSLContext dslContext = DSL.using(new DefaultConfiguration().set(SQLDialect.H2));

// Build jooqx legacy sql executor
LegacyJooqx jooqx = LegacyJooqx.builder()
                              .vertx(Vertx.vertx())
                              .dsl(dslContext)
                              .sqlClient(client)
                              .build();

// It is table class in database that is generated by jOOQ
Authors table = DefaultCatalog.DEFAULT_CATALOG.DEFAULT_SCHEMA.AUTHOR;
// Start query
jooqx.execute(jooqx.dsl().selectFrom(table), DSLAdapter.fetchMany(table), ar -> {
    // It is AuthorRecords class that is generated by jOOQ
    AuthorRecords record = ar.result().get(0);
    System.out.println(record.getId());
    System.out.println(record.getName());
});
//output: 1
//output: zero88
```

- Reactive SQL client

```java
PgConnectOptions connectOptions = new PgConnectOptions()
  .setPort(5432)
  .setHost("the-host")
  .setDatabase("the-db")
  .setUser("user")
  .setPassword("secret");

// Pool options
PoolOptions poolOptions = new PoolOptions().setMaxSize(5);

// Create the client pool
PgPool client = PgPool.pool(connectOptions, poolOptions);

// Init jOOQ DSL context
DSLContext dslContext = DSL.using(new DefaultConfiguration().set(SQLDialect.POSTGRES));

// Build jooqx reactive sql executor
ReactiveJooqx jooqx = ReactiveJooqx.builder().vertx(vertx)
                                            .dsl(dslContext)
                                            .sqlClient(client)
                                            .build();

// It is table class in database that is generated by jOOQ
Authors table = DefaultCatalog.DEFAULT_CATALOG.DEFAULT_SCHEMA.AUTHOR;
// Start query
SelectConditionStep<Record1<Integer>> query = jooqx.dsl()
                                                .selectCount()
                                                .from(table)
                                                .where(table.COUNTRY.eq("USA"));
jooqx.execute(query, DSLAdapter.fetchCount(query.asTable()), ar ->  System.out.println(ar.result()));
//output: 10
```

Interesting? Please checkout more features, [here](FEATURES.md) and [java-doc](https://zero88.github.io/jooqx/docs/javadoc/index.html)

## Contributions

Please go through on [develop](DEVELOP.md) for how to setup environment

## Short Note

Might you know, [vertx-jooq](https://github.com/jklingsporn/vertx-jooq) is another library for `vert.x` and `jOOQ` integration was started in a longtime ago.

I already used it, too. However, in a while I realized it doesn't meet my requirements for dynamic SQL.
Also, this library is narrow a power of `jOOQ DSL` and SQL feature likes `paging`, `batch`, `transaction`, `complex query with join/having/group`, etc. It is quite hard to extend due to mostly depends on `Data Access Object (DAO)` pattern, and the generation time.

Always have a different way to make it better, then I made this library to bring `jOOQ DSL` is first citizen and seamlessly combined with `Vert.x SQL client`.
