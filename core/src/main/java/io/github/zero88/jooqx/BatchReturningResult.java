package io.github.zero88.jooqx;

import java.util.List;

import org.jetbrains.annotations.NotNull;

/**
 * Batch result includes returning record
 *
 * @param <R> Type of records
 * @since 1.0.0
 */
public interface BatchReturningResult<R> extends BatchResult {

    @NotNull
    List<R> getRecords();

}
