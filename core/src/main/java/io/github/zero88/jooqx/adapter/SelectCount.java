package io.github.zero88.jooqx.adapter;

import java.util.Optional;

import org.jetbrains.annotations.NotNull;
import org.jooq.Record1;
import org.jooq.TableLike;

/**
 * Select count result adapter that defines output in {@code Integer} type
 *
 * @see SelectAdhocOneResult
 * @see SQLResultOneAdapter
 * @since 1.0.0
 */
public final class SelectCount extends SelectAdhocOneResult<Record1<Integer>, Integer> {

    public SelectCount(@NotNull TableLike<Record1<Integer>> table) {
        super(RecordFactory.byTable(table));
    }

    @Override
    protected Integer convert(Record1<Integer> record) {
        return Optional.ofNullable(record).map(r -> r.get(0, Integer.class)).orElse(0);
    }

}
