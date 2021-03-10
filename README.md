# Vert.x jOOQ DSL

![build](https://github.com/zero88/vertx-jooq-dsl/workflows/build-release/badge.svg?branch=main)
![GitHub release (latest SemVer)](https://img.shields.io/github/v/release/zero88/vertx-jooq-dsl?sort=semver)
![Sonatype Nexus (Releases)](https://img.shields.io/nexus/r/io.github.zero88/vertx-jooq-dsl?server=https%3A%2F%2Foss.sonatype.org%2F)
![Sonatype Nexus (Snapshots)](https://img.shields.io/nexus/s/io.github.zero88/vertx-jooq-dsl?server=https%3A%2F%2Foss.sonatype.org%2F)

[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=zero88_vertx-jooq-dsl&metric=sqale_rating)](https://sonarcloud.io/dashboard?id=zero88_vertx-jooq-dsl)
[![Reliability Rating](https://sonarcloud.io/api/project_badges/measure?project=zero88_vertx-jooq-dsl&metric=reliability_rating)](https://sonarcloud.io/dashboard?id=zero88_vertx-jooq-dsl)
[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=zero88_vertx-jooq-dsl&metric=security_rating)](https://sonarcloud.io/dashboard?id=zero88_vertx-jooq-dsl)
[![Duplicated Lines (%)](https://sonarcloud.io/api/project_badges/measure?project=zero88_vertx-jooq-dsl&metric=duplicated_lines_density)](https://sonarcloud.io/dashboard?id=zero88_vertx-jooq-dsl)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=zero88_vertx-jooq-dsl&metric=coverage)](https://sonarcloud.io/dashboard?id=zero88_vertx-jooq-dsl)

Vert.x jOOQ DSL uses a reactive and non-blocking sql connection from [Vert.x](https://vertx.io/docs/#databases) and keep
powerful from [jOOQ DSL](https://www.jooq.org) monster

## Features

`vertx-jooq-dsl` Provide uniform API for working on both

- [x] Vertx [Legacy SQL client](https://vertx.io/docs/vertx-jdbc-client/java/#_legacy_jdbc_client_api)
- [x] Vertx [Reactive SQL client](https://github.com/eclipse-vertx/vertx-sql-client)

`vertx-jooq-dsl` supports:

- [x] Lightweight and SPI
- [x] Strict type
- [x] Native `jOOQ DSL` API
- [x] Any database with `JDBC` driver both legacy and reactive mode
- [x] Any database with `reactive client` that `Vert.x` support (`JDBCPool` is `reactive JDBC driver` then almost database should work properly)
- [x] JsonObject record
- [x] Row/record transformation to `jOOQ` record
- [x] A unified format for `exception` and able to replace/integrate seamlessly by your current application exception
- [x] `CRUD` with prepared query by `jOOQ DSL`
- [x] Complex query as `join`, `with`, `having to` clause and strict type
- [x] Batch insert/update/merge
- [x] Transaction
  - [?] Nested transaction (not yet tested, but API is available)
  - [ ] Transaction rollback condition
  - [ ] Transaction annotation
- [ ] Procedure
- [ ] DAO
- [?] Resource Query Language (RQL) from [rql-jooq](https://github.com/zero88/universal-rsql)
- [ ] Rxified API
- [ ] Row streaming
- [ ] Publish/subscribe
- [x] Test fixtures API to easy setup test for your application testing or produce a minimum reproducer

## Usage

To use `vertx-jooq-dsl` add the following dependency to the dependencies section of your build descriptor:

- `Maven` (in your `pom.xml`):

```xml

<dependency>
    <groupId>io.github.zero88</groupId>
    <artifactId>vertx-jooq-dsl</artifactId>
    <version>1.0.0</version>
</dependency>
```

- `Gradle` (in your `build.gradle`):

```groovy
dependencies {
    compile("io.github.zero88:vertx-jooq-dsl:1.0.0")
}
```

**Hint**

`vertx-jooq-dsl` is only depended on 3 main libraries:

- `io.vertx:vertx-core`
- `org.jooq:jooq`
- `org.slf4j:slf4j-api`

Adding this `vertx-jooq-dsl` library JAR will not automatically add a `database driver` JAR to your project. You should
ensure that your project also has a suitable `database driver` as a dependency.

For example:

- With `legacy JDBC` and connecting to `MySQL` driver

```groovy
dependencies {
    compile("mysql:mysql-connector-java:8.0.23")
    // It is recommendation to use HikariCP instead of c3p0
    compile("com.zaxxer:HikariCP:4.0.2")
    compile("io.vertx:vertx-jdbc-client:4.0.2") {
        exclude("com.mchange")
    }
    compile("io.github.zero88:vertx-jooq-dsl:1.0.0")
}
```

- With `reactive PostgreSQL` client

```groovy
dependencies {
    compile("io.vertx:vertx-pg-client:4.0.2")
    compile("io.github.zero88:vertx-jooq-dsl:1.0.0")
}
```

- With `reactive JDBC` client and `H2`

```groovy
dependencies {
    compile("com.h2database:h2:1.4.200")
    // Agroal pool - Default in Vertx SQL client - Not yet has alternatives
    compile("io.agroal:agroal-pool:1.9")
    compile("io.vertx:vertx-sql-client:4.0.2")
    compile("io.github.zero88:vertx-jooq-dsl:1.0.0")
}
```

## Getting started

### Simple query

- Legacy SQL client

```java
JsonObject sqlOption = new JsonObject();
LegacySQLExecutor executor = LegacySQLExecutor.builder()
                                              .vertx(vertx)
                                              .dsl(DSL.using(new DefaultConfiguration().set(SQLDialect.H2)))
                                              .sqlClient(JDBCClient create(vertx, sqlOption))
                                              .build();
executor.execute(executor.dsl().selectFrom(AUTHORS), 
                 LegacyDSLAdapter.instance().fetchMany(AUTHORS),
                 ar -> {
                    AuthorRecords record = ar.result().get(0);
                    System.out.println(record.getId());
                    System.out.println(record.getName());
                 });
```

- Reactive SQL client

### Result Data transformation

### Batch

### Using transactions

### Exception
