package io.zero88.jooqx;

import io.vertx.core.Vertx;
import io.vertx.junit5.VertxTestContext;
import io.vertx.sqlclient.PoolOptions;

import lombok.NonNull;

public interface SQLClientProvider<S> extends HasSQLClient<S> {

    @NonNull S createSqlClient(Vertx vertx, VertxTestContext ctx, SQLConnectionOption connOpt);

    void closeClient(VertxTestContext context);

    default PoolOptions poolOptions() {
        return new PoolOptions();
    }

}
