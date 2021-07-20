package io.zero88.jooqx;

import org.jetbrains.annotations.NotNull;
import org.testcontainers.containers.JdbcDatabaseContainer;

import io.vertx.core.json.JsonObject;
import io.zero88.jooqx.provider.DBProvider;

import lombok.NonNull;

/**
 * Provides database in Docker container
 *
 * @param <DB> Type of Database Container
 * @see DBProvider
 */
public interface DBContainerProvider<DB extends JdbcDatabaseContainer<?>> extends DBProvider<DB> {

    /**
     * Init Database container
     *
     * @param imageName Database image name
     */
    @NotNull DB initDBContainer(String imageName);

    @Override
    default @NonNull JsonObject createConnOptions(DB container) {
        return new SQLConnectionOption().setHost(container.getHost())
                                        .setPort(container.getMappedPort(defaultPort()))
                                        .setDatabase(container.getDatabaseName())
                                        .setUser(container.getUsername())
                                        .setPassword(container.getPassword())
                                        .setJdbcUrl(container.getJdbcUrl())
                                        .setDriverClassName(container.getDriverClassName())
                                        .toJson();
    }

    /**
     * For example: {@code 3306} for {@code MySQL}, {@code 5432} for {@code PostgreSQL}
     *
     * @return Default database port
     */
    int defaultPort();

}
