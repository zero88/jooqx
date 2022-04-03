package io.zero88.jooqx.adapter;

import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jooq.Record;
import org.jooq.TableLike;

import io.zero88.jooqx.adapter.SQLResultAdapter.SQLResultListAdapter;

/**
 * Select Adhoc adapter for list result is a base class for custom {@code Select list} implementations in client code.
 *
 * @see SQLResultAdapter.SQLResultListAdapter
 * @since 1.0.0
 */
public abstract class SelectAdhocListResult<T extends TableLike<? extends Record>, R>
    extends SQLResultAdapterImpl<T, List<R>> implements SQLResultListAdapter<T, R> {

    protected SelectAdhocListResult(@NotNull T table) {
        super(table);
    }

}
