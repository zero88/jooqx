package io.github.zero88.integtest.jooqx.pg;

import java.util.stream.Stream;

import org.jetbrains.annotations.NotNull;
import org.jooq.SQLDialect;
import org.jooq.Schema;

import io.github.zero88.jooqx.JooqSQL;
import io.github.zero88.jooqx.SQLConnectionOption;
import io.github.zero88.jooqx.SQLTestHelper;
import io.vertx.junit5.VertxTestContext;

public interface PostgreSQLHelper<S extends Schema> extends JooqSQL<S>, SQLTestHelper {

    default void prepareDatabase(VertxTestContext context, JooqSQL<?> jooqSql, SQLConnectionOption connOption,
                                 String... otherDataFiles) {
        SQLTestHelper.super.prepareDatabase(context, jooqSql, connOption,
                                            Stream.concat(Stream.of("pg_schema.sql"), Stream.of(otherDataFiles))
                                                  .toArray(String[]::new));
    }

    @Override
    default @NotNull SQLDialect dialect() {
        return SQLDialect.POSTGRES;
    }

}
