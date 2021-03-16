package io.zero88.jooqx.adapter;

import org.jooq.Record;
import org.jooq.TableLike;

import io.zero88.jooqx.adapter.SQLResultAdapter.SQLResultOneAdapter;

import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;

/**
 * Select Adhoc adapter for one result is a base class for custom {@code Select one} implementations in client code.
 *
 * @see SQLResultAdapter.SQLResultOneAdapter
 * @since 1.0.0
 */
@Getter
@Accessors(fluent = true)
public abstract class SelectAdhocOneResult<T extends TableLike<? extends Record>, R> extends SQLResultAdapterImpl<T, R>
    implements SQLResultOneAdapter<T, R> {

    protected SelectAdhocOneResult(@NonNull T table) {
        super(table);
    }

}
