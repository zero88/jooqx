package io.github.zero88.jooqx.spi.pg;

import org.jetbrains.annotations.NotNull;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

import io.github.zero88.jooqx.DBContainerProvider;
import io.github.zero88.jooqx.HasDBProvider;

public interface PgSQLDBProvider extends DBContainerProvider<PostgreSQLContainer<?>>,
                                         HasDBProvider<PostgreSQLContainer<?>,
                                                          DBContainerProvider<PostgreSQLContainer<?>>> {

    @Override
    default @NotNull PostgreSQLContainer<?> init() {
        final String dbImage = System.getProperty("dbImage", "10-alpine");
        return initDBContainer("postgres:" + dbImage);
    }

    @Override
    default @NotNull PostgreSQLContainer<?> initDBContainer(String imageName) {
        return new PostgreSQLContainer<>(DockerImageName.parse(imageName)).withDatabaseName("foo")
                                                                          .withUsername("foo")
                                                                          .withPassword("secret");
    }

    @Override
    default int defaultPort() {
        return PostgreSQLContainer.POSTGRESQL_PORT;
    }

    @Override
    default PgSQLDBProvider dbProvider() {
        return this;
    }

}
