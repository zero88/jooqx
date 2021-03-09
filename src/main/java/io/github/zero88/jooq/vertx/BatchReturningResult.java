package io.github.zero88.jooq.vertx;

import java.util.List;

import org.jooq.Query;

import io.github.zero88.jooq.vertx.adapter.SelectListResultAdapter;
import io.vertx.core.Handler;

import lombok.Getter;
import lombok.NonNull;

/**
 * Batch result includes returning record
 * @param <R> Type of records
 * @see VertxReactiveBatchExecutor#batchExecute(Query, BindBatchValues, SelectListResultAdapter, Handler)
 * @since 1.0.0
 */
@Getter
public final class BatchReturningResult<R> extends BatchResult {

    @NonNull
    private final List<R> records;

    public BatchReturningResult(int total, @NonNull List<R> rs) {
        super(total, rs.size());
        this.records = rs;
    }

}
