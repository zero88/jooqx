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

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import lombok.NonNull;

/**
 * SQL test helper for initializing the test database schema via {@code HikariCP} and plain {@code jOOQ}
 *
 * @param <S> Type of schema
 * @see Schema
 */
public interface JooqSQL<S extends Schema> extends JooqDSLProvider, HasSQLDialect {

    /**
     * Get test database schema
     *
     * @return test schema
     */
    S schema();

    @Override
    default DSLContext dsl() {
        return JooqDSLProvider.create(dialect()).dsl();
    }

    /**
     * Create Hikari data source
     *
     * @param option sql connection options
     * @return Hikari data source
     */
    default HikariDataSource createDataSource(SQLConnectionOption option) {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(option.getJdbcUrl());
        hikariConfig.setUsername(option.getUser());
        hikariConfig.setPassword(option.getPassword());
        hikariConfig.setDriverClassName(option.getDriverClassName());
        return new HikariDataSource(hikariConfig);
    }

    /**
     * Close Hikari datasource
     *
     * @param dataSource datasource
     */
    default void closeDataSource(HikariDataSource dataSource) {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }

    /**
     * Prepare database when tear-up test case
     *
     * @param context Vertx test context
     * @param dsl     dsl
     * @param files   SQL files
     */
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
