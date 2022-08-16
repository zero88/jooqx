package io.github.zero88.jooqx;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.NotNull;

import io.vertx.codegen.annotations.GenIgnore;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.sqlclient.SqlResult;

/**
 * Represents for a collector that collects {@code Vert.x SQL batch result} to an expectation output
 *
 * @param <R> Type of each row in batch result
 * @see JooqxResultCollector
 * @since 2.0.0
 */
@VertxGen
public interface JooqxBatchCollector<R> extends SQLBatchCollector<SqlResult<List<R>>> {

    static <R> JooqxBatchCollector<R> create() { return new JooqxBatchCollector<R>() { }; }

    @Override
    default int batchResultSize(@NotNull SqlResult<List<R>> batchResult) {
        return reduce(batchResult).size();
    }

    /**
     * Collect to batch returning result
     *
     * @param bindValues  the bind batch values
     * @param batchResult the batch result
     * @return batch returning result
     * @see BindBatchValues
     * @see BatchReturningResult
     * @see SqlResult
     */
    @GenIgnore
    default BatchReturningResult<R> batchReturningResult(@NotNull BindBatchValues bindValues,
                                                         @NotNull SqlResult<List<R>> batchResult) {
        return BatchReturningResult.create(bindValues.size(), reduce(batchResult));
    }

    /**
     * Reduce batch result into list
     *
     * @param batchResult the {@code Vert.x} batch result
     * @return list result
     * @see SqlResult
     */
    @GenIgnore
    default List<R> reduce(SqlResult<List<R>> batchResult) {
        SqlResult<List<R>> rs = batchResult;
        final List<R> br = new ArrayList<>();
        do {
            br.add(rs.value().stream().findFirst().orElse(null));
        } while ((rs = rs.next()) != null);
        return br;
    }

}
