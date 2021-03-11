# Development

## Prerequisite

- Docker
- Java

## Testing

Out of the box, the integration test suite runs a `Docker container` using [`TestContainers`](https://www.testcontainers.org/) by default.

Before test, it is required to generate [database schema](integtest/src/test/resources) to `Java class` by `jOOQ` codegen.

Get started 

```bash
./generateJooq.sh

./gradlew build test
```
