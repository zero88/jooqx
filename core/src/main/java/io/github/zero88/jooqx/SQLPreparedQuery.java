package io.github.zero88.jooqx;

import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jooq.Configuration;
import org.jooq.Param;
import org.jooq.Query;

import io.github.zero88.jooqx.datatype.DataTypeMapperRegistry;

/**
 * Represents for SQL prepared query that transforms jOOQ Query to Vertx SQL prepared query
 *
 * @param <B> Type of Vertx bind value holder
 * @see LegacySQLPreparedQuery
 * @see JooqxPreparedQuery
 * @since 1.0.0
 */
public interface SQLPreparedQuery<B> {

    /**
     * Generate jOOQ query to sql query in String
     *
     * @param configuration jOOQ configuration
     * @param query         jOOQ query
     * @return sql
     * @see Query
     */
    @NotNull String sql(@NotNull Configuration configuration, @NotNull Query query);

    /**
     * Capture jOOQ param in jOOQ query and convert to Vertx bind value holder
     *
     * @param query          jOOQ query
     * @param mapperRegistry Data type mapper registry
     * @return bind value holder
     * @see Param
     * @see Query
     * @see DataTypeMapperRegistry
     */
    @NotNull B bindValues(@NotNull Query query, @NotNull DataTypeMapperRegistry mapperRegistry);

    /**
     * Capture jOOQ param in jOOQ query and convert to Vertx bind value holder
     *
     * @param query           jOOQ query
     * @param bindBatchValues bind batch values
     * @param mapperRegistry  Data type mapper registry
     * @return list of bind value holder
     * @apiNote It is used for batch execution
     * @see BindBatchValues
     * @see SQLBatchExecutor#batch(Query, BindBatchValues)
     * @see DataTypeMapperRegistry
     */
    @NotNull List<B> bindValues(@NotNull Query query, @NotNull BindBatchValues bindBatchValues,
                                @NotNull DataTypeMapperRegistry mapperRegistry);

}
