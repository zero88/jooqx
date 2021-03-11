# jOOQ.x - Vertx jOOQ DSL

![build](https://github.com/zero88/vertx-jooq-dsl/workflows/build-release/badge.svg?branch=main)
![GitHub release (latest SemVer)](https://img.shields.io/github/v/release/zero88/jooqx?sort=semver)
![Sonatype Nexus (Releases)](https://img.shields.io/nexus/r/io.github.zero88/jooqx?nexusVersion=3&server=https%3A%2F%2Foss.sonatype.org)
![Sonatype Nexus (Snapshots)](https://img.shields.io/nexus/s/io.github.zero88/jooqx?server=https%3A%2F%2Foss.sonatype.org)

[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=zero88_jooqx&metric=sqale_rating)](https://sonarcloud.io/dashboard?id=zero88_jooqx)
[![Reliability Rating](https://sonarcloud.io/api/project_badges/measure?project=zero88_jooqx&metric=reliability_rating)](https://sonarcloud.io/dashboard?id=zero88_jooqx)
[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=zero88_jooqx&metric=security_rating)](https://sonarcloud.io/dashboard?id=zero88_jooqx)
[![Duplicated Lines (%)](https://sonarcloud.io/api/project_badges/measure?project=zero88_jooqx&metric=duplicated_lines_density)](https://sonarcloud.io/dashboard?id=zero88_jooqx)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=zero88_jooqx&metric=coverage)](https://sonarcloud.io/dashboard?id=zero88_jooqx)

`jooqx` leverages the power of typesafe SQL from [jOOQ DSL](https://www.jooq.org) and running on SQL connection in a reactive and non-blocking of `SQL driver` from [Vert.x](https://vertx.io/docs/#databases)

## Features

`jooqx` Provide uniform API for working on both

- [x] Vertx [Legacy SQL client](https://vertx.io/docs/vertx-jdbc-client/java/#_legacy_jdbc_client_api)
- [x] Vertx [Reactive SQL client](https://github.com/eclipse-vertx/vertx-sql-client)

`jooqx` supports:

- [x] Lightweight and SPI
- [x] Typesafe
- [x] Native `jOOQ DSL` API
- [x] Any database with `JDBC` driver and combine with legacy SQL
- [x] Any database with `Vert.x reactive client`(`JDBCPool` is `reactive JDBC driver` so almost databases should work
  properly)
- [x] JsonObject record
- [x] Row/record transformation to `jOOQ` record
- [x] A unified format for `exception` and able to replace/integrate seamlessly by your current application exception
- [x] `CRUD` with prepared query by `jOOQ DSL`
- [x] Complex query as `join`, `with`, `having to`, etc clause and strict type
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

To use `jooqx` add the following dependency to the dependencies section of your build descriptor:

- `Maven` (in your `pom.xml`):

```xml

<dependency>
    <groupId>io.github.zero88</groupId>
    <artifactId>jooqx-core</artifactId>
    <version>1.0.0</version>
</dependency>
```

- `Gradle` (in your `build.gradle`):

```groovy
dependencies {
    compile("io.github.zero88:jooqx-core:1.0.0")
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
    compile("mysql:mysql-connector-java:8.0.23")
    // It is recommendation to use HikariCP instead of c3p0
    compile("com.zaxxer:HikariCP:4.0.2")
    compile("io.vertx:vertx-jdbc-client:4.0.2") {
        exclude("com.mchange")
    }
    compile("io.github.zero88:jooqx-core:1.0.0")
}
```

- With `reactive PostgreSQL` client

```groovy
dependencies {
    compile("io.vertx:vertx-pg-client:4.0.2")
    compile("io.github.zero88:jooqx-core:1.0.0")
}
```

- With `reactive JDBC` client and `H2`

```groovy
dependencies {
    compile("com.h2database:h2:1.4.200")
    // Agroal pool - Default in Vertx SQL client - Not yet has alternatives
    compile("io.agroal:agroal-pool:1.9")
    compile("io.vertx:vertx-sql-client:4.0.2")
    compile("io.github.zero88:jooqx-core:1.0.0")
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
LegacySQLExecutor executor = LegacySQLExecutor.builder().vertx(Vertx.vertx())
                                                        .dsl(dslContext)
                                                        .sqlClient(client)
                                                        .build();

// It is table class in database that is generated by jOOQ
io.github.zero88.jooq.vertx.integtest.h2.Authors table = DefaultCatalog.DEFAULT_CATALOG.DEFAULT_SCHEMA.AUTHOR;
// Start query
executor.execute(executor.dsl().selectFrom(table), LegacyDSLAdapter.instance().fetchMany(table),
                ar -> {
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
ReactiveSQLExecutor<PgPool> executor = ReactiveSQLExecutor.<PgPool>builder().vertx(vertx)
                                                                            .dsl(dslContext)
                                                                            .sqlClient(client)
                                                                            .build();

// It is table class in database that is generated by jOOQ
Authors table = DefaultCatalog.DEFAULT_CATALOG.DEFAULT_SCHEMA.AUTHOR;
// Start query
SelectConditionStep<Record1<Integer>> query = executor.dsl()
                                                      .selectCount()
                                                      .from(table)
                                                      .where(table.COUNTRY.eq("USA"));
executor.execute(query, ReactiveDSLAdapter.instance().fetchCount(query.asTable()), ar ->  System.out.println(ar.result()));
//output: 10
```

### Result Data transformation

#### To Json Record

```java
SelectForUpdateStep<AuthorsRecord> query = executor.dsl()
                                                .selectFrom(table)
                                                .where(table.COUNTRY.eq("USA"))
                                                .orderBy(table.NAME.desc())
                                                .limit(1)
                                                .offset(1);
executor.execute(query, ReactiveDSLAdapter.instance().fetchJsonRecord(query.asTable()), ar -> System.out.println(ar.result.toJson()));
// output: {"id":8,"name":"Christian Wenz","country":"USA"}
```

#### To Record or POJO

```java
SelectWhereStep<AuthorsRecord> query = executor.dsl().selectFrom(table);
//Authors is POJO class that generated by jOOQ
executor.execute(query, ReactiveDSLAdapter.instance().fetchMany(table, Authors.class), ar -> {
    List<Authors> authors = ar.result()
    Authors author = authors.get(0);
    System.out.println(author.getId());
    System.out.println(author.getCountry());
});
// output: 1
// output: UK
```

#### By jOOQ fields

```java
final Books table = catalog().PUBLIC.BOOKS;
final InsertResultStep<BooksRecord> insert = executor.dsl()
                                                    .insertInto(table, table.TITLE).values("aha")
                                                    .returning();
executor.execute(insert, ReactiveDSLAdapter.instance().fetchOne(table, Collections.singletonList(table.ID)), ar -> {
    Record record = ar.result();
    System.out.println(record.getValue(0));
});
// output: 10
```

Please checkout 2 `DSL adapter`: [ReactiveDSLAdapter](https://github.com/zero88/jooqx/blob/main/src/main/java/io/github/zero88/jooq/vertx/ReactiveDSLAdapter.java) and [LegacyDSLAdapter](https://github.com/zero88/jooqx/blob/main/src/main/java/io/github/zero88/jooq/vertx/LegacyDSLAdapter.java) to see which method that suite for you.

### Batch

```java
final Checkpoint flag = ctx.checkpoint(3);
final Books table = catalog().PUBLIC.BOOKS;
BooksRecord rec1 = new BooksRecord().setTitle("b1");
BooksRecord rec2 = new BooksRecord().setTitle("b2");
BooksRecord rec3 = new BooksRecord().setTitle("qwe");

final BindBatchValues bindValues = new BindBatchValues().register(table.TITLE).add(rec1, rec2, rec3);
final InsertResultStep<BooksRecord> insert = executor.dsl()
                                                    .insertInto(table)
                                                    .set(bindValues.getDummyValues())
                                                    .returning();
executor.batch(insert, bindValues, ar -> System.out.println(ar.result));
// {total = 2, success = 2}
```

With `reactive SQL client`, it is possible to returning list of records and able to transformation as above.

```java
final InsertResultStep<AuthorsRecord> insert = executor.dsl()
                                                    .insertInto(table)
                                                    .set(bindValues.getDummyValues())
                                                    .returning(table.ID);
executor.batch(insert, bindValues,
                       ReactiveDSLAdapter.instance().batch(table, executor.dsl().newRecord(table.ID)), handler);
```

### Using transactions

```java
executor.transaction().run(tx -> {
    final InsertResultStep<BooksRecord> q1 = tx.dsl()
                                                .insertInto(table, table.ID, table.TITLE)
                                                .values(Arrays.asList(DSL.defaultValue(table.ID), "abc"))
                                                .returning(table.ID);
    final InsertResultStep<BooksRecord> q2 = tx.dsl()
                                                .insertInto(table, table.ID, table.TITLE)
                                                .values(Arrays.asList(DSL.defaultValue(table.ID), "xyz"))
                                                .returning(table.ID);
    return tx.execute(q1, ReactiveDSLAdapter.instance().fetchOne(table))
             .flatMap(b1 -> tx.execute(q2, ReactiveDSLAdapter.instance().fetchOne(table)));
}, ar -> {});
```

### Exception handler

Basically, `exception` in execution time will be thrown by each particular `jdbc` driver or `reactive SQL driver`, it can be spaghetti code, dealing with `exception`, then `jooqx` is able to centralize any exception with properly `SQL state` that thanks to `DataAccessException` in `jOOQ`.

It is easy to configure when building `executor`

```java
// with Reactive PostgreSQL sql client exception
ReactiveSQLExecutor<PgPool> executor = ReactiveSQLExecutor.<PgPool>builder().vertx(vertx)
                                                                            .dsl(dsl)
                                                                            .sqlClient(sqlClient)
                                                                            .errorConverter(new PgErrorConverter())
                                                                            .build();

// with JDBC sql client exception

//reactive
ReactiveSQLExecutor<JDBCPool> executor = ReactiveSQLExecutor.<JDBCPool>builder().vertx(vertx)
                                                                                .dsl(dsl)
                                                                                .sqlClient(sqlClient)
                                                                                .errorConverter(new JDBCErrorConverter())
                                                                                .build();
//legacy
LegacySQLExecutor executor = LegacySQLExecutor.builder().vertx(vertx)
                                                        .dsl(dsl)
                                                        .sqlClient(sqlClient)
                                                        .errorConverter(new JDBCErrorConverter())
                                                        .build();
```

And so more, you can convert to your existing application exception (must `extends RuntimeException`) by

```java
new JDBCErrorConverter().to(dataAccessException -> new DatabaseError(ErrorCode.Duplicate, dataAccessException))
```
