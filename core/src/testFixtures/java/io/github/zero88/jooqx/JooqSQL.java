package io.github.zero88.jooqx;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

import org.jetbrains.annotations.NotNull;
import org.jooq.DSLContext;
import org.jooq.Schema;

import io.github.zero88.jooqx.provider.HasSQLDialect;
import io.vertx.junit5.VertxTestContext;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

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
    default @NotNull DSLContext dsl() {
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
    default void prepareDatabase(@NotNull VertxTestContext context, @NotNull DSLContext dsl, @NotNull String... files) {
        Arrays.stream(files).forEach(file -> {
            try {
                InputStream is = Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream(file));
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
                    dsl.execute(reader.lines().collect(Collectors.joining("\n")));
                }
            } catch (IOException | NullPointerException e) {
                context.failNow(e);
            }
        });
        context.completeNow();
    }

}
