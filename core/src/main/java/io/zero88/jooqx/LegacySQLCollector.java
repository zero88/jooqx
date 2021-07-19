package io.zero88.jooqx;

import java.util.List;

import io.vertx.codegen.annotations.GenIgnore;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.ext.sql.ResultSet;
import io.zero88.jooqx.LegacySQLImpl.LegacySQLRC;
import io.zero88.jooqx.adapter.RowConverterStrategy;

import lombok.NonNull;

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
    int batchResultSize(@NonNull List<Integer> batchResult);

    @Override
    @GenIgnore(GenIgnore.PERMITTED_TYPE)
    @NonNull <T, R> List<R> collect(@NonNull ResultSet resultSet, @NonNull RowConverterStrategy<T, R> strategy);

}
