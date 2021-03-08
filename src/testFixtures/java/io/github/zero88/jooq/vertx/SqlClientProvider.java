package io.github.zero88.jooq.vertx;

import io.vertx.core.Vertx;
import io.vertx.junit5.VertxTestContext;
import io.vertx.sqlclient.PoolOptions;

public interface SqlClientProvider<S> extends HasSqlClient<S> {

    S createConnection(Vertx vertx, VertxTestContext ctx, SqlConnectionOption connOpt);

    S createPool(Vertx vertx, VertxTestContext ctx, SqlConnectionOption opt);

    default boolean usePool() {
        return false;
    }

    default PoolOptions poolOptions() {
        return new PoolOptions();
    }

}
