package io.zero88.jooqx;

import java.util.List;

import org.jetbrains.annotations.NotNull;

import io.vertx.codegen.annotations.VertxGen;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import io.zero88.jooqx.adapter.RowConverterStrategy;

/**
 * Reactive result batch converter
 *
 * @see JooqxResultCollector
 * @since 1.1.0
 */
@VertxGen
public interface JooqxBatchCollector
    extends JooqxResultCollector, SQLBatchCollector<RowSet<Row>, RowSet<Row>> {

    @Override
    @NotNull <T, R> List<R> collect(@NotNull RowSet<Row> resultSet, @NotNull RowConverterStrategy<T, R> strategy);

    @Override
    int batchResultSize(@NotNull RowSet<Row> batchResult);

}
