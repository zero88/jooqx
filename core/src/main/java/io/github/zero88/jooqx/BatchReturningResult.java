package io.github.zero88.jooqx;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.jetbrains.annotations.NotNull;

/**
 * Batch result includes returning record
 *
 * @param <R> Type of records
 * @since 1.0.0
 */
public interface BatchReturningResult<R> extends BatchResult {

    static <R> BatchReturningResult<R> create(int total, List<R> results) {
        return new BatchReturningResultImpl<>(total, Optional.ofNullable(results).orElseGet(ArrayList::new));
    }

    @NotNull
    List<R> getRecords();

}
