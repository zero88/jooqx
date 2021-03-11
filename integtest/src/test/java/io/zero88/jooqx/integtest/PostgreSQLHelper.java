package io.zero88.jooqx.integtest;

import java.util.Arrays;
import java.util.stream.Stream;

import org.jooq.SQLDialect;

import io.vertx.junit5.VertxTestContext;
import io.vertx.sqlclient.SqlClient;
import io.zero88.jooqx.JooqSQL;
import io.zero88.jooqx.SQLConnectionOption;
import io.zero88.jooqx.SQLErrorConverter;
import io.zero88.jooqx.SQLTestHelper;
import io.zero88.jooqx.integtest.pgsql.DefaultCatalog;
import io.zero88.jooqx.spi.PostgreSQLReactiveTest;
import io.zero88.jooqx.spi.pg.PgErrorConverter;

import lombok.NonNull;

public interface PostgreSQLHelper extends JooqSQL<DefaultCatalog>, SQLTestHelper {

    default void prepareDatabase(VertxTestContext ctx, JooqSQL<?> jooqSql, SQLConnectionOption connOption,
                                 String... otherDataFiles) {
        SQLTestHelper.super.prepareDatabase(ctx, jooqSql, connOption,
                                            Stream.concat(Stream.of("pg_schema.sql", "pg_data.sql"),
                                                          Arrays.stream(otherDataFiles)).toArray(String[]::new));
    }

    @Override
    default DefaultCatalog catalog() {
        return DefaultCatalog.DEFAULT_CATALOG;
    }

    @Override
    default @NonNull SQLDialect dialect() {
        return SQLDialect.POSTGRES;
    }

    interface UsePgSQLErrorConverter<S extends SqlClient> extends PostgreSQLReactiveTest<S> {

        @Override
        default SQLErrorConverter<? extends Throwable, ? extends RuntimeException> createErrorConverter() {
            return new PgErrorConverter();
        }

    }

}
