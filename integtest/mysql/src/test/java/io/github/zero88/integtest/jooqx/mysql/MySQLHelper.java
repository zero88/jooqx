package io.github.zero88.integtest.jooqx.mysql;

import java.sql.Connection;
import java.util.stream.Stream;

import org.jetbrains.annotations.NotNull;
import org.jooq.ConnectionProvider;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;

import io.github.zero88.jooqx.JooqSQL;
import io.github.zero88.jooqx.SQLConnectionOption;
import io.github.zero88.jooqx.SQLTestHelper;
import io.github.zero88.jooqx.spi.mysql.MySQLInitializer;
import io.github.zero88.sample.model.mysql.DefaultCatalog;
import io.github.zero88.sample.model.mysql.Test;
import io.vertx.junit5.VertxTestContext;

public interface MySQLHelper extends JooqSQL<Test>, SQLTestHelper {

    @Override
    default @NotNull SQLDialect dialect() {
        return SQLDialect.MYSQL;
    }

    @Override
    default Test schema() {
        return DefaultCatalog.DEFAULT_CATALOG.TEST;
    }

    default void prepareDatabase(VertxTestContext context, SQLConnectionOption connOption, String... dataSQLFiles) {
        String jdbcUrl = connOption.getJdbcUrl() + "?allowMultiQueries=true";
        SQLTestHelper.super.prepareDatabase(context, this, connOption.setJdbcUrl(jdbcUrl),
                                            Stream.concat(Stream.of("mysql_schema.sql"), Stream.of(dataSQLFiles))
                                                  .toArray(String[]::new));
    }

    @Override
    default void prepareDatabase(@NotNull VertxTestContext context, @NotNull DSLContext dsl, @NotNull String... files) {
        final ConnectionProvider connectionProvider = dsl.configuration().connectionProvider();
        final Connection conn = connectionProvider.acquire();
        MySQLInitializer.runScript(conn, files);
        connectionProvider.release(conn);
        context.completeNow();
    }

}
