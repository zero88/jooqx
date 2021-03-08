package io.github.zero88.jooq.vertx;

import java.util.function.Supplier;

import org.testcontainers.containers.JdbcDatabaseContainer;

import lombok.NonNull;

public interface DBProvider<S> extends Supplier<S> {

    @NonNull SqlConnectionOption connOpt(S server);

    interface DBMemoryProvider extends DBProvider<String> {

        @Override
        default @NonNull SqlConnectionOption connOpt(String jdbcUrl) {
            return SqlConnectionOption.builder().jdbcUrl(jdbcUrl).driverClassName(driverClassName()).build();
        }

        @NonNull String driverClassName();

    }


    interface DBContainerProvider<K extends JdbcDatabaseContainer<?>> extends DBProvider<K> {

        @Override
        default @NonNull SqlConnectionOption connOpt(K server) {
            return SqlConnectionOption.builder()
                                      .host(server.getHost())
                                      .port(server.getMappedPort(defaultPort()))
                                      .database(server.getDatabaseName())
                                      .username(server.getUsername())
                                      .password(server.getPassword())
                                      .jdbcUrl(server.getJdbcUrl())
                                      .driverClassName(server.getDriverClassName())
                                      .build();
        }

        K get(String imageName);

        int defaultPort();

    }

}
