package io.zero88.jooqx.adapter;

import org.jetbrains.annotations.NotNull;
import org.jooq.Record;
import org.jooq.TableLike;

import io.zero88.jooqx.adapter.SQLResultAdapter.SQLResultOneAdapter;

/**
 * Select Adhoc adapter for one result is a base class for custom {@code Select one} implementations in client code.
 *
 * @see SQLResultAdapter.SQLResultOneAdapter
 * @since 1.0.0
 */
public abstract class SelectAdhocOneResult<T extends TableLike<? extends Record>, R> extends SQLResultAdapterImpl<T, R>
    implements SQLResultOneAdapter<T, R> {

    protected SelectAdhocOneResult(@NotNull T table) {
        super(table);
    }

}
