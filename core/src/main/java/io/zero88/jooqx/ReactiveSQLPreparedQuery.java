package io.zero88.jooqx;

import java.util.List;

import org.jooq.Configuration;
import org.jooq.Query;

import io.vertx.codegen.annotations.GenIgnore;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.sqlclient.Tuple;
import io.zero88.jooqx.datatype.DataTypeMapperRegistry;

import lombok.NonNull;

/**
 * Represents for a converter that transforms {@code jOOQ param} to {@code Vertx Reactive SQL} bind value
 *
 * @since 1.0.0
 */
@VertxGen
public interface ReactiveSQLPreparedQuery extends SQLPreparedQuery<Tuple> {

    @Override
    @GenIgnore(GenIgnore.PERMITTED_TYPE)
    @NonNull String sql(@NonNull Configuration configuration, @NonNull Query query);

    @Override
    @GenIgnore(GenIgnore.PERMITTED_TYPE)
    @NonNull Tuple bindValues(@NonNull Query query, @NonNull DataTypeMapperRegistry mapperRegistry);

    @Override
    @GenIgnore(GenIgnore.PERMITTED_TYPE)
    @NonNull List<Tuple> bindValues(@NonNull Query query, @NonNull BindBatchValues bindBatchValues,
                                    @NonNull DataTypeMapperRegistry mapperRegistry);

}
