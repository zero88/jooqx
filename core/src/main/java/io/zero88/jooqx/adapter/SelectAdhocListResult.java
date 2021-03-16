package io.zero88.jooqx.adapter;

import java.util.List;

import org.jooq.Record;
import org.jooq.TableLike;

import io.zero88.jooqx.adapter.SQLResultAdapter.SQLResultListAdapter;

import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;

/**
 * Select Adhoc adapter for list result is a base class for custom {@code Select list} implementations in client code.
 *
 * @see SQLResultAdapter.SQLResultListAdapter
 * @since 1.0.0
 */
@Getter
@Accessors(fluent = true)
public abstract class SelectAdhocListResult<T extends TableLike<? extends Record>, R>
    extends SQLResultAdapterImpl<T, List<R>> implements SQLResultListAdapter<T, R> {

    protected SelectAdhocListResult(@NonNull T table) {
        super(table);
    }

}
