package io.github.zero88.jooqx.spi.mysql;

import org.jetbrains.annotations.NotNull;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.utility.DockerImageName;

import io.github.zero88.jooqx.DBContainerProvider;
import io.github.zero88.jooqx.HasDBProvider;
import io.github.zero88.jooqx.SQLTestHelper;

public interface MySQLDBProvider extends DBContainerProvider<MySQLContainer<?>>,
                                         HasDBProvider<MySQLContainer<?>, DBContainerProvider<MySQLContainer<?>>> {

    @Override
    default @NotNull MySQLContainer<?> init() {
        final String dbVersion = System.getProperty("dbVersion", SQLTestHelper.getCurrentDBVersion("8.3"));
        return initDBContainer("mysql:" + dbVersion);
    }

    @Override
    default @NotNull MySQLContainer<?> initDBContainer(String imageName) {
        return new MySQLContainer<>(DockerImageName.parse(imageName)).withDatabaseName("test")
                                                                     .withUsername("root")
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
