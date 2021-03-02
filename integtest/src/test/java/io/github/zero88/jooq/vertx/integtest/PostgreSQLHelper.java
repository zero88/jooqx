package io.github.zero88.jooq.vertx.integtest;

import org.jooq.SQLDialect;
import org.testcontainers.containers.JdbcDatabaseContainer;

import io.github.zero88.jooq.vertx.JooqSql;
import io.github.zero88.jooq.vertx.integtest.pgsql.DefaultCatalog;
import io.github.zero88.utils.Strings;
import io.vertx.junit5.VertxTestContext;

import com.zaxxer.hikari.HikariDataSource;

import lombok.NonNull;

public interface PostgreSQLHelper extends JooqSql<DefaultCatalog>, SqlTestHelper {

    default void prepareDatabase(VertxTestContext ctx, JooqSql<?> jooqSql, JdbcDatabaseContainer<?> server) {
        HikariDataSource dataSource = jooqSql.createDataSource(server);
        jooqSql.prepareDatabase(ctx, jooqSql.dsl(dataSource, jooqSql.dialect()), "pg_schema.sql", "pg_data.sql");
        dataSource.close();
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
