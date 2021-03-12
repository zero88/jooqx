package io.zero88.jooqx;

import io.vertx.ext.jdbc.JDBCClient;
import io.vertx.ext.sql.ResultSet;
import io.vertx.ext.sql.SQLClient;
import io.vertx.ext.sql.SQLConnection;
import io.vertx.ext.sql.SQLOperations;

import lombok.NonNull;

/**
 * Represents for an executor that executes {@code jOOQ query} on {@code Vertx legacy JDBC client} connection
 *
 * @see SQLClient
 * @see SQLConnection
 * @see SQLOperations
 * @see JDBCClient
 * @see ResultSet
 * @since 1.0.0
 */
public interface LegacyJooqx extends LegacySQLImpl.LegacyInternal<SQLClient> {

    static LegacySQLImpl.LegacyJooqxImpl.LegacyJooqxImplBuilder<?, ?> builder() {
        return LegacySQLImpl.LegacyJooqxImpl.builder();
    }

    @Override
    @SuppressWarnings("unchecked")
    @NonNull LegacyJooqxTx transaction();

}
