package io.zero88.jooqx;

import java.util.function.Function;

import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.sql.ResultSet;
import io.vertx.ext.sql.SQLConnection;
import io.zero88.jooqx.LegacySQLImpl.LegacyInternal;

import lombok.NonNull;

/**
 * Represents for an executor that use in legacy SQL transaction
 *
 * @since 1.0.0
 */
public interface LegacyJooqxTx extends LegacyInternal<SQLConnection>,
                                       SQLTxExecutor<SQLConnection, JsonArray, ResultSet, LegacySQLCollector,
                                                        LegacyJooqxTx> {

    @Override
    <X> Future<X> run(@NonNull Function<LegacyJooqxTx, Future<X>> function);

    @Override
    @SuppressWarnings("unchecked")
    default @NonNull LegacyJooqxTx transaction() {
        return this;
    }

}
