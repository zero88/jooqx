package io.github.zero88.jooq.vertx;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jooq.Catalog;
import org.jooq.DSLContext;

import io.vertx.junit5.VertxTestContext;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import lombok.NonNull;

public interface JooqSql<T extends Catalog> extends JooqDSLProvider, HasDSLProvider {

    T catalog();

    @Override
    default JooqDSLProvider dslProvider() {
        return this;
    }

    default HikariDataSource createDataSource(SqlConnectionOption option) {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(option.getJdbcUrl());
        hikariConfig.setUsername(option.getUsername());
        hikariConfig.setPassword(option.getPassword());
        hikariConfig.setDriverClassName(option.getDriverClassName());
        return new HikariDataSource(hikariConfig);
    }

    default void closeDataSource(HikariDataSource dataSource) {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }

    default void prepareDatabase(@NonNull VertxTestContext context, @NonNull DSLContext dsl, @NonNull String... files) {
        Arrays.stream(files).forEach(file -> {
            try {
                final Path path = Paths.get(getClass().getClassLoader().getResource(file).toURI());
                try (Stream<String> lines = Files.lines(path)) {
                    dsl.execute(lines.collect(Collectors.joining("\n")));
                }
            } catch (URISyntaxException | IOException e) {
                context.failNow(e);
            }
        });
        context.completeNow();
    }

}
