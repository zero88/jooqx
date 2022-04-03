package io.zero88.jooqx;

import java.util.List;

import org.jetbrains.annotations.NotNull;

import io.vertx.codegen.annotations.GenIgnore;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.ext.sql.ResultSet;
import io.zero88.jooqx.LegacySQLImpl.LegacySQLRC;
import io.zero88.jooqx.adapter.RowConverterStrategy;

/**
 * Represents for Legacy SQL result set collector
 *
 * @since 1.0.0
 */
@VertxGen
public interface LegacySQLCollector extends SQLResultCollector<ResultSet>, SQLBatchCollector<ResultSet, List<Integer>> {

    static LegacySQLCollector create() {
        return new LegacySQLRC();
    }

    @Override
    int batchResultSize(@NotNull List<Integer> batchResult);

    @Override
    @GenIgnore(GenIgnore.PERMITTED_TYPE)
    @NotNull <T, R> List<R> collect(@NotNull ResultSet resultSet, @NotNull RowConverterStrategy<T, R> strategy);

}
