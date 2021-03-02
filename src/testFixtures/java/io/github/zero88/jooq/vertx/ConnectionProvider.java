package io.github.zero88.jooq.vertx;

import java.util.function.Supplier;
import javax.sql.DataSource;

import org.testcontainers.containers.JdbcDatabaseContainer;

import io.vertx.core.Vertx;
import io.vertx.ext.jdbc.JDBCClient;
import io.vertx.ext.sql.SQLClient;
import io.vertx.junit5.VertxTestContext;
import io.vertx.sqlclient.SqlClient;

public interface ConnectionProvider<S> extends Supplier<DBContainerProvider<?>> {

    S createConnection(Vertx vertx, VertxTestContext ctx, JdbcDatabaseContainer<?> server);

    S createPool(Vertx vertx, VertxTestContext ctx, JdbcDatabaseContainer<?> server);

    default boolean usePool() {
        return false;
    }

    interface ReactiveSqlTest extends ConnectionProvider<SqlClient> {}


    interface LegacyJdbcSqlTest extends ConnectionProvider<SQLClient> {

        DataSource getDataSource();

        @Override
        default boolean usePool() {
            return true;
        }

        @Override
        default SQLClient createConnection(Vertx vertx, VertxTestContext ctx, JdbcDatabaseContainer<?> server) {
            throw new UnsupportedOperationException("DataSource is in Pool as default");
        }

        @Override
        default SQLClient createPool(Vertx vertx, VertxTestContext ctx, JdbcDatabaseContainer<?> server) {
            final JDBCClient client = JDBCClient.create(vertx, getDataSource());
            ctx.completeNow();
            return client;
        }

    }

}
