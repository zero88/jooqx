package io.github.zero88.jooq.vertx;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.sql.DataSource;

import org.jooq.Catalog;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.jooq.impl.DefaultConfiguration;
import org.testcontainers.containers.JdbcDatabaseContainer;

import io.vertx.junit5.VertxTestContext;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import lombok.NonNull;

public interface JooqSql<T extends Catalog> extends HasJooqDialect {

    T catalog();

    default DSLContext dsl(SQLDialect dialect) {
        return DSL.using(new DefaultConfiguration().set(dialect));
    }

    default DSLContext dsl(DataSource dataSource, SQLDialect dialect) {
        return DSL.using(new DefaultConfiguration().derive(dialect).derive(dataSource));
    }

    default HikariDataSource createDataSource(JdbcDatabaseContainer<?> server) {
        //        final JsonObject config = new JsonObject().put("provider_class", HikariCPDataSourceProvider.class
        //        .getName())
        //                                                  .put("driverClassName", server.getDriverClassName())
        //                                                  .put("jdbcUrl", server.getJdbcUrl())
        //                                                  .put("username", server.getUsername())
        //                                                  .put("password", server.getPassword());
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(server.getJdbcUrl());
        hikariConfig.setUsername(server.getUsername());
        hikariConfig.setPassword(server.getPassword());
        hikariConfig.setDriverClassName(server.getDriverClassName());
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
