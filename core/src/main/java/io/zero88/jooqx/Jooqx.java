package io.zero88.jooqx;

import org.jetbrains.annotations.NotNull;
import org.jooq.DSLContext;

import io.vertx.codegen.annotations.GenIgnore;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.Vertx;
import io.vertx.sqlclient.Pool;
import io.zero88.jooqx.JooqxSQLImpl.JooqxBuilderImpl;
import io.zero88.jooqx.datatype.DataTypeMapperRegistry;

/**
 * Represents for an executor that executes {@code jOOQ query} on {@code Vertx reactive SQL pool}
 *
 * @see Pool
 * @since 2.0.0
 */
@VertxGen
public interface Jooqx extends JooqxBase<Pool> {

    /**
     * Create a builder
     *
     * @return jooqx builder
     * @see JooqxBuilder
     */
    static JooqxBuilder builder() { return new JooqxBuilderImpl(); }

    @Override
    @NotNull Vertx vertx();

    @Override
    @GenIgnore(GenIgnore.PERMITTED_TYPE)
    @NotNull DSLContext dsl();

    /**
     * @see Pool
     */
    @Override
    @NotNull Pool sqlClient();

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

    /**
     * @see JooqxTx
     */
    @SuppressWarnings("unchecked")
    @Override
    @NotNull JooqxTx transaction();

}
