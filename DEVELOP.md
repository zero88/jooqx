# Development

## Prerequisite

- Docker
- Java

## Testing

Out of the box, the integration test suite runs a `Docker database container`
using [TestContainers](https://www.testcontainers.org/) or memory database (`H2`, `SQLite`)
on [Junit5](https://junit.org/junit5/) and [Vert.x Junit 5 integration](https://vertx.io/docs/vertx-junit5/java/) by
default.

Before test, it is required to generate [database schema](integtest/src/test/resources) to `Java class` by `jOOQ`
codegen.

Let's begin

```bash
./generateJooq.sh

./gradlew build test
```

## Naming rule

`[DB][Type::3 char][Topic]Test`

In which:

- `DB`  Database name: H2, Pg(PostgreSQL), MySQL, etc...
- `Type` is Legacy(`LeG`) or Reactive(`ReA`) JDBC client
- `Topic` is which topic for testing

Examples:

- `H2LeGRelTest`: `H2` Database `Legacy` JDBC `Relationship` Topic `Test`
- `H2ReANumericTest`: `H2` Database `Reactive` JDBC `Numeric` Topic `Test`
- `PgReATemporalTest`: `PostgreSQL` Database `Reactive` JDBC `temporal-data-type` topic `Test`

## How to write new test

To write new test or `MCVE` (Minimal Complete Verifiable Example) for a specific Database and on which JDBC
client (`legacy` or `reactive`) type, please use `jooqx-core` test fixtures. Also provide a suitable `database driver`
as a dependency.

- Maven

```xml

<dependency>
  <groupId>io.github.zero88</groupId>
  <artifactId>jooqx-core</artifactId>
  <version>1.0.0</version>
  <classifier>testFixtures</classifier>
  <type>jar</type>
</dependency>

<!-- SQLite -->
<dependencies>
<dependency>
  <groupId>org.xerial</groupId>
  <artifactId>sqlite-jdbc</artifactId>
  <version>3.34.0</version>
</dependency>
</dependencies>
```

- Gradle

```groovy
api(testFixtures("io.github.zero88:jooqx-core"))
// sqlite
api("org.xerial:sqlite-jdbc:3.34.0") )
```

For `legacy JDBC`, you can extend base class `LegacyDBMemoryTest` or `LegacyDBContainerTest`. For example:

```java
class H2LeGSomethingTest extends LegacyDBMemoryTest<HikariCPDataSourceProvider>
  implements H2DBProvider, H2SQLHelper, JDBCLegacyHikariProvider, UseJdbcErrorConverter {

  @Test
  void test_something(VertxTestContext ctx) {

  }

}
```

For `reactive JDBC`, you can extend base class `ReactiveDBMemoryTest` or `ReactiveDBContainerTest`

```java
class PgReASomethingTest extends ReactiveDBContainerTest<PgPool, PostgreSQLContainer<?>>
  implements PgPoolProvider, PostgreSQLHelper, UsePgSQLErrorConverter {

  @Test
  void test_something(VertxTestContext ctx) {

  }

}
```
