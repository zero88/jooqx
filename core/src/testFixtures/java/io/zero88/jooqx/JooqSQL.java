package io.zero88.jooqx;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jooq.DSLContext;
import org.jooq.Schema;

import io.vertx.junit5.VertxTestContext;
import io.zero88.jooqx.provider.HasSQLDialect;
import io.zero88.jooqx.provider.JooqDSLProvider;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import lombok.NonNull;

public interface JooqSQL<S extends Schema> extends JooqDSLProvider, HasSQLDialect {

    S schema();

    @Override
    default DSLContext dsl() {
        return JooqDSLProvider.create(this).dsl();
    }

    default HikariDataSource createDataSource(SQLConnectionOption option) {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(option.getJdbcUrl());
        hikariConfig.setUsername(option.getUser());
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
                final Path path = Paths.get(
                    Objects.requireNonNull(getClass().getClassLoader().getResource(file)).toURI());
                try (Stream<String> lines = Files.lines(path)) {
                    dsl.execute(lines.collect(Collectors.joining("\n")));
                }
            } catch (URISyntaxException | IOException | NullPointerException e) {
                context.failNow(e);
            }
        });
        context.completeNow();
    }

}
