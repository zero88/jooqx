package io.zero88.jooqx;

import java.util.List;

import io.vertx.codegen.annotations.GenIgnore;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import io.zero88.jooqx.ReactiveSQLImpl.ReactiveSQLRC;
import io.zero88.jooqx.adapter.RowConverterStrategy;

import lombok.NonNull;

/**
 * Reactive result set converter
 *
 * @since 1.0.0
 */
@VertxGen
public interface ReactiveSQLResultCollector extends SQLResultCollector<RowSet<Row>> {

    static ReactiveSQLResultCollector create() {
        return new ReactiveSQLRC();
    }

    @Override
    @GenIgnore(GenIgnore.PERMITTED_TYPE)
    @NonNull <T, R> List<R> collect(@NonNull RowSet<Row> resultSet, @NonNull RowConverterStrategy<T, R> strategy);

}
