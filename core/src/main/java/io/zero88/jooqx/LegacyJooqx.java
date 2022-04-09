package io.zero88.jooqx;

import org.jetbrains.annotations.NotNull;
import org.jooq.DSLContext;

import io.vertx.codegen.annotations.GenIgnore;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.Vertx;
import io.vertx.ext.jdbc.JDBCClient;
import io.vertx.ext.sql.ResultSet;
import io.vertx.ext.sql.SQLClient;
import io.vertx.ext.sql.SQLConnection;
import io.vertx.ext.sql.SQLOperations;
import io.zero88.jooqx.LegacySQLImpl.LegacyInternal;
import io.zero88.jooqx.LegacySQLImpl.LegacyJooqxBuilderImpl;
import io.zero88.jooqx.datatype.DataTypeMapperRegistry;

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
@VertxGen
public interface LegacyJooqx extends LegacyInternal<SQLClient> {

    /**
     * Create a builder
     *
     * @return legacy jooqx builder
     * @see LegacyJooqxBuilder
     */
    static LegacyJooqxBuilder builder() {
        return new LegacyJooqxBuilderImpl();
    }

    @Override
    @NotNull Vertx vertx();

    @Override
    @GenIgnore(GenIgnore.PERMITTED_TYPE)
    @NotNull DSLContext dsl();

    /**
     * @see SQLClient
     */
    @Override
    @NotNull SQLClient sqlClient();

    /**
     * @see LegacySQLPreparedQuery
     */
    @Override
    @NotNull LegacySQLPreparedQuery preparedQuery();

    /**
     * @see LegacySQLCollector
     */
    @Override
    @NotNull LegacySQLCollector resultCollector();

    @Override
    @GenIgnore(GenIgnore.PERMITTED_TYPE)
    @NotNull SQLErrorConverter errorConverter();

    @Override
    @GenIgnore(GenIgnore.PERMITTED_TYPE)
    @NotNull DataTypeMapperRegistry typeMapperRegistry();

    /**
     * @see LegacyJooqxTx
     */
    @Override
    @SuppressWarnings("unchecked")
    @NotNull LegacyJooqxTx transaction();

}
