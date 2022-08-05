package io.github.zero88.jooqx.adapter;

import java.util.Optional;

import org.jetbrains.annotations.NotNull;
import org.jooq.Record1;
import org.jooq.TableLike;

/**
 * Select exists result adapter that defines output in {@code Boolean} type
 *
 * @see SelectAdhocOneResult
 * @see SQLResultOneAdapter
 * @since 1.0.0
 */
public final class SelectExists extends SelectAdhocOneResult<Record1<Integer>, Boolean> {

    public SelectExists(@NotNull TableLike<Record1<Integer>> table) {
        super(RecordFactory.byTable(table));
    }

    @Override
    protected Boolean convert(Record1<Integer> record) {
        return Optional.ofNullable(record).map(r -> r.get(0, Integer.class)).map(nb -> nb > 0).orElse(false);
    }

}
