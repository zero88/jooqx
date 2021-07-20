package io.zero88.jooqx;

import java.util.List;

import org.jooq.Configuration;
import org.jooq.Query;

import io.vertx.codegen.annotations.GenIgnore;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.json.JsonArray;
import io.zero88.jooqx.LegacySQLImpl.LegacySQLPQ;
import io.zero88.jooqx.datatype.DataTypeMapperRegistry;

import lombok.NonNull;

/**
 * Represents for a converter that transforms {@code jOOQ param} to {@code Vertx legacy SQL} bind value
 *
 * @since 1.0.0
 */
@VertxGen
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
    @NonNull String sql(@NonNull Configuration configuration, @NonNull Query query);

    @Override
    @GenIgnore(GenIgnore.PERMITTED_TYPE)
    @NonNull JsonArray bindValues(@NonNull Query query, @NonNull DataTypeMapperRegistry mapperRegistry);

    @Override
    @GenIgnore(GenIgnore.PERMITTED_TYPE)
    @NonNull List<JsonArray> bindValues(@NonNull Query query, @NonNull BindBatchValues bindBatchValues,
                                        @NonNull DataTypeMapperRegistry mapperRegistry);

}
