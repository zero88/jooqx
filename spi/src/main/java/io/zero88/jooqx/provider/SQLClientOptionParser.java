package io.zero88.jooqx.provider;

import java.util.Objects;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import io.vertx.core.json.JsonObject;
import io.vertx.sqlclient.PoolOptions;

/**
 * A parser helps convert raw SQL connection option to a specific SQL driver connection option
 *
 * @param <T> Type of SQL driver connection option
 * @since 2.0.0
 */
public interface SQLClientOptionParser<T> {

    /**
     * Parse a json SQL connection options to the specific connection options depends on SQL driver
     *
     * @param connOptions SQL connection option
     * @return a specific SQL connection option
     * @throws IllegalArgumentException if any invalid in connection option
     */
    @NotNull T parseConn(@NotNull JsonObject connOptions);

    /**
     * Parse a json SQL pool options
     *
     * @param poolOptions SQL pool options
     * @return a SQL pool option
     * @throws IllegalArgumentException if any invalid in connection option
     * @see PoolOptions
     */
    default @NotNull PoolOptions parsePool(@Nullable JsonObject poolOptions) {
        return Objects.isNull(poolOptions) ? new PoolOptions() : new PoolOptions(poolOptions);
    }

}
