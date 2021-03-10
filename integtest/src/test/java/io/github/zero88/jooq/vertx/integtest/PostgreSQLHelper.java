package io.github.zero88.jooq.vertx.integtest;

import org.jooq.SQLDialect;

import io.github.zero88.jooq.vertx.JooqSQL;
import io.github.zero88.jooq.vertx.SQLConnectionOption;
import io.github.zero88.jooq.vertx.integtest.pgsql.DefaultCatalog;
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
