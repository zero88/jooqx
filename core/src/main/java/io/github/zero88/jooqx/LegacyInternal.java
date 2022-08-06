package io.github.zero88.jooqx;

import org.jetbrains.annotations.NotNull;

import io.vertx.core.json.JsonArray;
import io.vertx.ext.sql.SQLOperations;

@Deprecated
public interface LegacyInternal<S extends SQLOperations>
    extends SQLExecutor<S, JsonArray, LegacySQLPreparedQuery, LegacySQLCollector> {

    @Override
    @NotNull LegacySQLPreparedQuery preparedQuery();

    @Override
    @NotNull LegacySQLCollector resultCollector();

}
