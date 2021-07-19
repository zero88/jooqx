package io.zero88.jooqx.provider;

import org.jetbrains.annotations.NonNls;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.sqlclient.PoolOptions;

public interface SQLClientProvider<S> extends HasSQLClient<S> {

    /**
     * Define SQL Pool options
     *
     * @return SQL pool options
     */
    @NonNls
    default PoolOptions poolOptions() {
        return new PoolOptions();
    }

    /**
     * Create SQL client
     *
     * @param vertx      vertx
     * @param connOption SQL connection option
     * @return a SQL client future
     */
    @NonNls Future<S> open(Vertx vertx, JsonObject connOption);

    /**
     * Close SQL client
     *
     * @return void future
     */
    @NonNls Future<Void> close();

}
