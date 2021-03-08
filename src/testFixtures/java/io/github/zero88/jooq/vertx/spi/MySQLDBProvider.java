package io.github.zero88.jooq.vertx.spi;

import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.utility.DockerImageName;

import io.github.zero88.jooq.vertx.DBProvider.DBContainerProvider;
import io.github.zero88.jooq.vertx.HasDBProvider;

public interface MySQLDBProvider extends DBContainerProvider<MySQLContainer<?>>,
                                         HasDBProvider<MySQLContainer<?>, DBContainerProvider<MySQLContainer<?>>> {

    @Override
    default MySQLContainer<?> get() {
        return get("mysql:8.0");
    }

    @Override
    default MySQLContainer<?> get(String imageName) {
        return new MySQLContainer<>(DockerImageName.parse(imageName)).withDatabaseName("foo")
                                                                     .withUsername("foo")
                                                                     .withPassword("secret");
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
