package io.zero88.jooqx.spi.pg;

import io.vertx.core.Vertx;
import io.vertx.junit5.VertxTestContext;
import io.vertx.pgclient.PgPool;
import io.zero88.jooqx.ReactiveTestDefinition.ReactiveSQLClientProvider;
import io.zero88.jooqx.SQLConnectionOption;

public interface PgPoolProvider extends ReactiveSQLClientProvider<PgPool>, PgSQLClientProvider {

    @Override
    default PgPool createSqlClient(Vertx vertx, VertxTestContext ctx, SQLConnectionOption connOpt) {
        final PgPool pool = PgPool.pool(vertx, connectionOptions(connOpt), poolOptions());
        ctx.completeNow();
        return pool;
    }

}
