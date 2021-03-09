package io.github.zero88.jooq.vertx;

import io.vertx.core.Vertx;
import io.vertx.junit5.VertxTestContext;
import io.vertx.sqlclient.PoolOptions;

import lombok.NonNull;

public interface SqlClientProvider<S> extends HasSqlClient<S> {

    @NonNull S createSqlClient(Vertx vertx, VertxTestContext ctx, SqlConnectionOption connOpt);

    void closeClient(VertxTestContext context);

    default PoolOptions poolOptions() {
        return new PoolOptions();
    }

}
