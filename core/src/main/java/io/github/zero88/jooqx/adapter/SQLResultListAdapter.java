package io.github.zero88.jooqx.adapter;

import java.util.List;

import org.jetbrains.annotations.NotNull;

/**
 * Indicates select many row
 *
 * @see SQLResultAdapter
 * @since 1.0.0
 */
public interface SQLResultListAdapter<ROW, EACH> extends SQLResultAdapter<ROW, List<EACH>> {

    @Override
    default @NotNull SelectStrategy strategy() {
        return SelectStrategy.MANY;
    }

    interface SQLResultIdentityListAdapter<ROW> extends SQLResultListAdapter<ROW, ROW> {

        @Override
        default List<ROW> collect(@NotNull List<ROW> records) { return records; }

    }

}
