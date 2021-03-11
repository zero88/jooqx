package io.zero88.jooqx.spi;

import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

import io.zero88.jooqx.DBProvider.DBContainerProvider;
import io.zero88.jooqx.HasDBProvider;

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
