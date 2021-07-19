package io.zero88.jooqx;

import java.util.function.Supplier;

import org.testcontainers.containers.JdbcDatabaseContainer;

import lombok.NonNull;

public interface DBProvider<S> extends Supplier<S> {

    @NonNull SQLConnectionOption connOpt(S server);

    interface DBMemoryProvider extends DBProvider<String> {

        @Override
        default @NonNull SQLConnectionOption connOpt(String jdbcUrl) {
            return new SQLConnectionOption().setJdbcUrl(jdbcUrl).setDriverClassName(driverClassName());
        }

        @NonNull String driverClassName();

    }


    interface DBContainerProvider<K extends JdbcDatabaseContainer<?>> extends DBProvider<K> {

        @Override
        default @NonNull SQLConnectionOption connOpt(K server) {
            return new SQLConnectionOption().setHost(server.getHost())
                                            .setPort(server.getMappedPort(defaultPort()))
                                            .setDatabase(server.getDatabaseName())
                                            .setUser(server.getUsername())
                                            .setPassword(server.getPassword())
                                            .setJdbcUrl(server.getJdbcUrl())
                                            .setDriverClassName(server.getDriverClassName());
        }

        K get(String imageName);

        int defaultPort();

    }

}
