package io.github.zero88.jooqx.adapter;

import java.util.List;

import org.jetbrains.annotations.NotNull;

/**
 * Indicates select only one row
 *
 * @since 1.0.0
 */
public interface SQLResultOneAdapter<ROW, RESULT> extends SQLResultAdapter<ROW, RESULT> {

    @Override
    default @NotNull SelectStrategy strategy() {
        return SelectStrategy.FIRST_ONE;
    }

    interface SQLResultIdentityOneAdapter<ROW> extends SQLResultOneAdapter<ROW, ROW> {

        default ROW collect(@NotNull List<ROW> records) { return records.stream().findFirst().orElse(null); }

    }

}
