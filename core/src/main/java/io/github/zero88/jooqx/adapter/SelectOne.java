package io.github.zero88.jooqx.adapter;

import org.jetbrains.annotations.NotNull;
import org.jooq.Record;

import io.github.zero88.jooqx.adapter.SQLResultOneAdapter.SQLResultIdentityOneAdapter;

/**
 * Select one result adapter that returns only one row
 *
 * @param <R> Type of output result
 * @see SQLResultOneAdapter
 * @since 1.0.0
 */
public final class SelectOne<R> extends SQLResultAdapterImpl<R, R> implements SQLResultIdentityOneAdapter<R> {

    public SelectOne(@NotNull RecordFactory<? extends Record, R> recordFactory) {
        super(recordFactory);
    }

}
