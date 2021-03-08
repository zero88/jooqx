package io.github.zero88.jooq.vertx.integtest;

import org.jooq.SQLDialect;

import io.github.zero88.jooq.vertx.JooqDSLProvider;
import io.github.zero88.jooq.vertx.JooqSql;
import io.github.zero88.jooq.vertx.SqlConnectionOption;
import io.github.zero88.jooq.vertx.integtest.pgsql.DefaultCatalog;
import io.github.zero88.utils.Strings;
import io.vertx.junit5.VertxTestContext;

import com.zaxxer.hikari.HikariDataSource;

import lombok.NonNull;

public interface PostgreSQLHelper extends JooqSql<DefaultCatalog>, SqlTestHelper {

    default void prepareDatabase(VertxTestContext ctx, JooqSql<?> jooqSql, SqlConnectionOption connOption) {
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
