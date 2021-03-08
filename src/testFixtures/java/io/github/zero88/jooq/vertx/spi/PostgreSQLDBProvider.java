package io.github.zero88.jooq.vertx.spi;

import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

import io.github.zero88.jooq.vertx.DBProvider.DBContainerProvider;
import io.github.zero88.jooq.vertx.HasDBProvider;

public interface PostgreSQLDBProvider extends DBContainerProvider<PostgreSQLContainer<?>>,
                                              HasDBProvider<PostgreSQLContainer<?>,
                                                               DBContainerProvider<PostgreSQLContainer<?>>> {

    @Override
    default PostgreSQLContainer<?> get() {
        return get("postgres:10-alpine");
    }

    @Override
    default PostgreSQLContainer<?> get(String imageName) {
        return new PostgreSQLContainer<>(DockerImageName.parse(imageName)).withDatabaseName("foo")
                                                                          .withUsername("foo")
                                                                          .withPassword("secret");
    }

    @Override
    default int defaultPort() {
        return PostgreSQLContainer.POSTGRESQL_PORT;
    }

    @Override
    default PostgreSQLDBProvider dbProvider() {
        return this;
    }

}
