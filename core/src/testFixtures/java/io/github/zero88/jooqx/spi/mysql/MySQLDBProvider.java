package io.github.zero88.jooqx.spi.mysql;

import org.jetbrains.annotations.NotNull;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.utility.DockerImageName;

import io.github.zero88.jooqx.DBContainerProvider;
import io.github.zero88.jooqx.HasDBProvider;

public interface MySQLDBProvider extends DBContainerProvider<MySQLContainer<?>>,
                                         HasDBProvider<MySQLContainer<?>, DBContainerProvider<MySQLContainer<?>>> {

    @Override
    default @NotNull MySQLContainer<?> init() {
        return initDBContainer("mysql:8.0");
    }

    @Override
    default @NotNull MySQLContainer<?> initDBContainer(String imageName) {
        return new MySQLContainer<>(DockerImageName.parse(imageName)).withDatabaseName("foo")
                                                                     .withUsername("foo")
                                                                     .withPassword("123")
                                                                     .withCommand("--bind-address=0.0.0.0");
    }

    @Override
    default int defaultPort() {
        return MySQLContainer.MYSQL_PORT;
    }

    @Override
    default MySQLDBProvider dbProvider() {
        return this;
    }

}
