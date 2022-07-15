package io.github.zero88.jooqx.adapter;

import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jooq.Record;

import io.github.zero88.jooqx.adapter.SQLResultListAdapter.SQLResultIdentityListAdapter;

/**
 * Select list result adapter that returns list of output
 *
 * @param <R> Type of each item in list result
 * @see SQLResultListAdapter
 * @since 1.0.0
 */
public final class SelectList<R> extends SQLResultAdapterImpl<R, List<R>> implements SQLResultIdentityListAdapter<R> {

    public SelectList(@NotNull RecordFactory<? extends Record, R> recordFactory) {
        super(recordFactory);
    }

}
