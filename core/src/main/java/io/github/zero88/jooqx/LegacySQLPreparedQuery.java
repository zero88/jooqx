package io.github.zero88.jooqx;

import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jooq.Configuration;
import org.jooq.Query;
import org.jooq.Routine;

import io.github.zero88.jooqx.LegacySQLImpl.LegacySQLPQ;
import io.github.zero88.jooqx.datatype.DataTypeMapperRegistry;
import io.vertx.codegen.annotations.GenIgnore;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.json.JsonArray;

/**
 * Represents for a converter that transforms {@code jOOQ param} to {@code Vertx legacy SQL} bind value
 *
 * @since 1.0.0
 */
@VertxGen
@Deprecated
public interface LegacySQLPreparedQuery extends SQLPreparedQuery<JsonArray> {

    /**
     * Create default legacy SQL prepare query
     *
     * @return a new instance of LegacySQLPreparedQuery
     */
    static LegacySQLPreparedQuery create() {
        return new LegacySQLPQ();
    }

    @Override
    @GenIgnore(GenIgnore.PERMITTED_TYPE)
    @NotNull String sql(@NotNull Configuration configuration, @NotNull Query query);

    @Override
    @GenIgnore(GenIgnore.PERMITTED_TYPE)
    @NotNull String sql(@NotNull Configuration configuration, @NotNull BlockQuery blockQuery);

    @Override
    @GenIgnore(GenIgnore.PERMITTED_TYPE)
    @NotNull JsonArray bindValues(@NotNull Query query, @NotNull DataTypeMapperRegistry mapperRegistry);

    @Override
    @GenIgnore(GenIgnore.PERMITTED_TYPE)
    @NotNull List<JsonArray> bindValues(@NotNull Query query, @NotNull BindBatchValues bindBatchValues,
                                        @NotNull DataTypeMapperRegistry mapperRegistry);

    @SuppressWarnings("rawtypes")
    @Override
    @GenIgnore(GenIgnore.PERMITTED_TYPE)
    @NotNull String routine(@NotNull Configuration configuration, @NotNull Routine routine);

    @SuppressWarnings("rawtypes")
    @Override
    @GenIgnore(GenIgnore.PERMITTED_TYPE)
    @NotNull JsonArray routineValues(@NotNull Routine routine, @NotNull DataTypeMapperRegistry mapperRegistry);

}
