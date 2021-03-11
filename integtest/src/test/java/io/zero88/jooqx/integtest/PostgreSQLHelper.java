package io.zero88.jooqx.integtest;

import org.jooq.SQLDialect;

import io.zero88.jooqx.JooqSQL;
import io.zero88.jooqx.SQLConnectionOption;
import io.zero88.jooqx.integtest.pgsql.DefaultCatalog;
import io.github.zero88.utils.Strings;
import io.vertx.junit5.VertxTestContext;

import com.zaxxer.hikari.HikariDataSource;

import lombok.NonNull;

public interface PostgreSQLHelper extends JooqSQL<DefaultCatalog>, SQLTestHelper {

    default void prepareDatabase(VertxTestContext ctx, JooqSQL<?> jooqSql, SQLConnectionOption connOption) {
        HikariDataSource dataSource = jooqSql.createDataSource(connOption);
        jooqSql.prepareDatabase(ctx, jooqSql.dsl(dataSource), "pg_schema.sql", "pg_data.sql");
        closeDataSource(dataSource);
        System.out.println(Strings.duplicate("=", 150));
    }

    @Override
    default DefaultCatalog catalog() {
        return DefaultCatalog.DEFAULT_CATALOG;
    }

    @Override
    default @NonNull SQLDialect dialect() {
        return SQLDialect.POSTGRES;
    }

}
