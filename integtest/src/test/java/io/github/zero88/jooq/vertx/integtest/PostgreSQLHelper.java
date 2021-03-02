package io.github.zero88.jooq.vertx.integtest;

import org.testcontainers.containers.JdbcDatabaseContainer;

import io.github.zero88.jooq.vertx.JooqSql;
import io.github.zero88.utils.Strings;
import io.vertx.junit5.VertxTestContext;

import com.zaxxer.hikari.HikariDataSource;

public class PostgreSQLHelper {

    public static void prepareDatabase(VertxTestContext ctx, JooqSql<?> jooqSql, JdbcDatabaseContainer<?> server) {
        HikariDataSource dataSource = jooqSql.createDataSource(server);
        jooqSql.prepareDatabase(ctx, jooqSql.dsl(dataSource, jooqSql.dialect()), "pg_schema.sql", "pg_data.sql");
        dataSource.close();
        System.out.println(Strings.duplicate("=", 150));
    }

}
