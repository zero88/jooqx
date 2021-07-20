package io.zero88.jooqx.provider;

import java.util.Objects;

import org.jetbrains.annotations.NotNull;

import io.vertx.core.Future;
import io.vertx.sqlclient.SqlClient;

/**
 * Reactive SQL client provider
 *
 * @param <S> Type of reactive SQL client
 * @see SqlClient
 * @since 1.1.0
 */
public interface ReactiveSQLClientProvider<S extends SqlClient> extends SQLClientProvider<S> {

    @Override
    default @NotNull Future<Void> close(S sqlClient) {
        return Objects.isNull(sqlClient) ? Future.succeededFuture() : sqlClient.close();
    }

}
