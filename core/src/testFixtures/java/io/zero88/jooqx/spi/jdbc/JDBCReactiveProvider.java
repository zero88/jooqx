package io.zero88.jooqx.spi.jdbc;

import io.vertx.core.Vertx;
import io.vertx.jdbcclient.JDBCConnectOptions;
import io.vertx.jdbcclient.JDBCPool;
import io.vertx.junit5.VertxTestContext;
import io.zero88.jooqx.ReactiveTestDefinition.ReactiveSQLClientProvider;
import io.zero88.jooqx.SQLConnectionOption;

public interface JDBCReactiveProvider extends ReactiveSQLClientProvider<JDBCPool> {

    @Override
    default JDBCPool createSqlClient(Vertx vertx, VertxTestContext ctx, SQLConnectionOption connOpt) {
        final JDBCPool pool = JDBCPool.pool(vertx, new JDBCConnectOptions().setJdbcUrl(connOpt.getJdbcUrl())
                                                                           .setDatabase(connOpt.getDatabase())
                                                                           .setUser(connOpt.getUsername())
                                                                           .setPassword(connOpt.getPassword()),
                                            poolOptions());
        ctx.completeNow();
        return pool;
    }

}
