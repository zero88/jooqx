package io.github.zero88.jooqx.adapter;

import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jooq.Record;

/**
 * Select Adhoc adapter for one result is a base class for custom {@code Select one} implementations in client code.
 *
 * @see SQLResultAdapter
 * @since 1.0.0
 */
public abstract class SelectAdhocOneResult<ROW, RESULT> extends SQLResultAdapterImpl<ROW, RESULT>
    implements SQLResultOneAdapter<ROW, RESULT> {

    protected SelectAdhocOneResult(@NotNull RecordFactory<? extends Record, ROW> recordFactory) {
        super(recordFactory);
    }

    public RESULT collect(@NotNull List<ROW> rows) { return convert(rows.stream().findFirst().orElse(null)); }

    protected abstract RESULT convert(@Nullable ROW row);

}
