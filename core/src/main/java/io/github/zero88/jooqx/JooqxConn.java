package io.github.zero88.jooqx;

import org.jetbrains.annotations.NotNull;
import org.jooq.DSLContext;

import io.github.zero88.jooqx.JooqxSQLImpl.JooqxConnBuilderImpl;
import io.github.zero88.jooqx.datatype.DataTypeMapperRegistry;
import io.vertx.codegen.annotations.GenIgnore;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.Vertx;
import io.vertx.sqlclient.SqlConnection;

/**
 * Represents for an executor that executes {@code jOOQ query} on {@code Vertx reactive SQL client} connection
 *
 * @see SqlConnection
 * @since 2.0.0
 */
@VertxGen
public interface JooqxConn extends JooqxBase<SqlConnection> {

    /**
     * Create a builder
     *
     * @return jooqx conn builder
     * @see JooqxConnBuilder
     */
    static JooqxConnBuilder builder() { return new JooqxConnBuilderImpl(); }

    @Override
    @NotNull Vertx vertx();

    @Override
    @GenIgnore(GenIgnore.PERMITTED_TYPE)
    @NotNull DSLContext dsl();

    /**
     * @see SqlConnection
     */
    @Override
    @NotNull SqlConnection sqlClient();

    /**
     * @see JooqxPreparedQuery
     */
    @Override
    @NotNull JooqxPreparedQuery preparedQuery();

    /**
     * @see JooqxResultCollector
     */
    @Override
    @NotNull JooqxResultCollector resultCollector();

    @Override
    @GenIgnore(GenIgnore.PERMITTED_TYPE)
    @NotNull SQLErrorConverter errorConverter();

    @Override
    @GenIgnore(GenIgnore.PERMITTED_TYPE)
    @NotNull DataTypeMapperRegistry typeMapperRegistry();

    @SuppressWarnings("unchecked")
    @Override
    @NotNull JooqxTx transaction();

    @Override
    @SuppressWarnings("unchecked")
    @NotNull JooqxSession session();

}
