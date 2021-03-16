package io.zero88.jooqx.adapter;

import org.jooq.Record;
import org.jooq.TableLike;

import io.zero88.jooqx.adapter.SQLResultAdapter.SelectOneStrategy;

import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;

/**
 * Select Adhoc result adapter is a base class for custom {@code Select} implementations in client code.
 *
 * @see SQLResultAdapter
 */
@Getter
@Accessors(fluent = true)
public abstract class SelectAdhocOneResultAdapter<T extends TableLike<? extends Record>, O>
    extends SQLResultAdapterImpl<T, O> implements SelectOneStrategy {

    protected SelectAdhocOneResultAdapter(@NonNull T table) {
        super(table);
    }

}
