package io.zero88.jooqx.provider;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

/**
 * SQL client provider
 *
 * @param <S> Type of SQL client
 * @since 1.1.0
 */
public interface SQLClientProvider<S> {

    /**
     * Create and open SQL client
     *
     * @param vertx       vertx
     * @param connOptions SQL connection options
     * @param poolOptions SQL pool options
     * @return a SQL client future
     */
    @NotNull Future<S> open(Vertx vertx, JsonObject connOptions, @Nullable JsonObject poolOptions);

    /**
     * Close SQL client
     *
     * @param sqlClient SQL client
     * @return void future
     */
    @NotNull Future<Void> close(S sqlClient);

}
