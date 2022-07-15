package io.github.zero88.jooqx.adapter;

import java.util.List;
import java.util.stream.Collectors;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jooq.Record;

/**
 * Select Adhoc adapter for list result is a base class for custom {@code Select list} implementations in client code.
 *
 * @see SQLResultListAdapter
 * @since 1.0.0
 */
public abstract class SelectAdhocListResult<ROW, EACH> extends SQLResultAdapterImpl<ROW, List<EACH>>
    implements SQLResultListAdapter<ROW, EACH> {

    protected SelectAdhocListResult(@NotNull RecordFactory<? extends Record, ROW> recordFactory) {
        super(recordFactory);
    }

    public List<EACH> collect(@NotNull List<ROW> records) {
        return records.stream().map(this::convert).collect(Collectors.toList());
    }

    protected abstract EACH convert(@Nullable ROW row);

}
