package io.github.zero88.jooqx;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.NotNull;

import io.vertx.codegen.annotations.GenIgnore;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import io.vertx.sqlclient.SqlResult;

/**
 * Represents for a collector that collects {@code Vert.x SQL batch result} to an expectation output
 *
 * @param <R> Type of each row in batch result
 * @see JooqxResultCollector
 * @since 2.0.0
 */
@VertxGen
public interface JooqxBatchCollector<R>
    extends JooqxResultCollector, SQLBatchCollector<RowSet<Row>, SqlResult<List<R>>> {

    @Override
    default int batchResultSize(@NotNull SqlResult<List<R>> batchResult) {
        return reduce(batchResult).size();
    }

    @GenIgnore
    default List<R> reduce(SqlResult<List<R>> batchResult) {
        final List<R> br = new ArrayList<>();
        SqlResult<List<R>> res = batchResult;
        do {
            br.add(res.value().stream().findFirst().orElse(null));
        } while ((res = res.next()) != null);
        return br;
    }

}
