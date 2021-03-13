package io.zero88.jooqx.adapter;

import org.jooq.Record1;
import org.jooq.TableLike;

import io.zero88.jooqx.SQLResultCollector;
import io.zero88.jooqx.adapter.SQLResultAdapter.SelectOne;
import io.zero88.jooqx.datatype.SQLDataTypeRegistry;

import lombok.NonNull;

/**
 * Select count result adapter that defines output in {@code Integer} type
 *
 * @see SelectAdhocResultAdapter
 * @see SelectOne
 * @since 1.0.0
 */
public final class SelectCountAdapter<RS, C extends SQLResultCollector<RS>>
    extends SelectAdhocResultAdapter<RS, C, TableLike<Record1<Integer>>, Integer> implements SelectOne {

    protected SelectCountAdapter(@NonNull TableLike<Record1<Integer>> table, @NonNull C converter) {
        super(table, converter);
    }

    @Override
    public Integer collect(@NonNull RS resultSet, @NonNull SQLDataTypeRegistry registry) {
        return converter().collect(resultSet, converterStrategy(registry))
                          .stream()
                          .findFirst()
                          .map(r -> r.get(0, Integer.class))
                          .orElse(0);
    }

    public static <RS, C extends SQLResultCollector<RS>> SelectCountAdapter<RS, C> count(
        @NonNull TableLike<Record1<Integer>> table, @NonNull C converter) {
        return new SelectCountAdapter<>(table, converter);
    }

}
