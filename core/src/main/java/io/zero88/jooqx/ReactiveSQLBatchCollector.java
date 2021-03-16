package io.zero88.jooqx;

import java.util.List;

import org.jooq.Record;

import io.vertx.codegen.annotations.VertxGen;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import io.zero88.jooqx.adapter.RowConverterStrategy;

import lombok.NonNull;

/**
 * Reactive result batch converter
 *
 * @see ReactiveSQLResultCollector
 * @since 1.0.0
 */
@VertxGen
public interface ReactiveSQLBatchCollector
    extends ReactiveSQLResultCollector, SQLBatchCollector<RowSet<Row>, RowSet<Row>> {

    @Override
    @NonNull <R extends Record, O> List<O> collect(@NonNull RowSet<Row> resultSet,
                                                   @NonNull RowConverterStrategy<R, O> strategy);

    @Override
    int batchResultSize(@NonNull RowSet<Row> batchResult);

}
