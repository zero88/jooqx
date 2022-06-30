package io.github.zero88.jooqx;

import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jooq.Configuration;
import org.jooq.Query;
import org.jooq.Routine;

import io.github.zero88.jooqx.JooqxSQLImpl.ReactiveSQLPQ;
import io.github.zero88.jooqx.datatype.DataTypeMapperRegistry;
import io.vertx.codegen.annotations.GenIgnore;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.sqlclient.Tuple;

/**
 * Represents for a converter that transforms {@code jOOQ param} to {@code Vertx Reactive SQL} bind value
 *
 * @since 2.0.0
 */
@VertxGen
public interface JooqxPreparedQuery extends SQLPreparedQuery<Tuple> {

    /**
     * Create default reactive SQL prepare query
     *
     * @return a new instance of ReactiveSQLPreparedQuery
     */
    static JooqxPreparedQuery create() {
        return new ReactiveSQLPQ();
    }

    @Override
    @GenIgnore(GenIgnore.PERMITTED_TYPE)
    @NotNull String sql(@NotNull Configuration configuration, @NotNull Query query);

    @Override
    @GenIgnore(GenIgnore.PERMITTED_TYPE)
    @NotNull Tuple bindValues(@NotNull Query query, @NotNull DataTypeMapperRegistry mapperRegistry);

    @Override
    @GenIgnore(GenIgnore.PERMITTED_TYPE)
    @NotNull List<Tuple> bindValues(@NotNull Query query, @NotNull BindBatchValues bindBatchValues,
                                    @NotNull DataTypeMapperRegistry mapperRegistry);

    @SuppressWarnings("rawtypes")
    @Override
    @GenIgnore(GenIgnore.PERMITTED_TYPE)
    @NotNull String routine(@NotNull Configuration configuration, @NotNull Routine routine);

    @SuppressWarnings("rawtypes")
    @Override
    @GenIgnore(GenIgnore.PERMITTED_TYPE)
    @NotNull Tuple routineValues(@NotNull Routine routine, @NotNull DataTypeMapperRegistry mapperRegistry);

}
