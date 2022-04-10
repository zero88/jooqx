package io.github.zero88.jooqx.provider;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

/**
 * SQL client provider
 *
 * @param <S> Type of SQL client
 * @since 2.0.0
 */
public interface SQLClientProvider<S> {

    /**
     * Defines SQL client class.
     * <p>
     * It helps for detecting and scanning in runtime
     *
     * @return SQL client class
     */
    String sqlClientClass();

    /**
     * Create and open SQL client
     *
     * @param vertx       vertx
     * @param connOptions SQL connection options
     * @param poolOptions SQL pool options
     * @return the SQL client future
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
