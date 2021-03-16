package io.zero88.jooqx.spi.mysql;

import io.vertx.core.Vertx;
import io.vertx.junit5.VertxTestContext;
import io.vertx.mysqlclient.MySQLPool;
import io.zero88.jooqx.ReactiveTestDefinition.ReactiveSQLClientProvider;
import io.zero88.jooqx.SQLConnectionOption;

public interface MySQLPoolProvider extends ReactiveSQLClientProvider<MySQLPool>, MySQLClientProvider {

    @Override
    default MySQLPool createSqlClient(Vertx vertx, VertxTestContext ctx, SQLConnectionOption connOpt) {
        final MySQLPool pool = MySQLPool.pool(vertx, connectionOptions(connOpt), poolOptions());
        ctx.completeNow();
        return pool;
    }

}
