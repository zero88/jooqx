package io.zero88.jooqx.adapter;

import org.jooq.Record;
import org.jooq.TableLike;

import io.zero88.jooqx.SQLResultCollector;
import io.zero88.jooqx.adapter.SQLResultAdapter.SelectOne;

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
public abstract class SelectAdhocOneResultAdapter<RS, C extends SQLResultCollector<RS>, T extends TableLike<? extends Record>, O>
    extends SQLResultAdapterImpl<RS, C, T, O> implements SelectOne {

    protected SelectAdhocOneResultAdapter(@NonNull T table, @NonNull C converter) {
        super(table, converter);
    }

}
