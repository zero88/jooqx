package io.zero88.jooqx.adapter;

import org.jooq.Record1;
import org.jooq.TableLike;

import io.zero88.jooqx.SQLResultCollector;
import io.zero88.jooqx.adapter.SQLResultAdapter.SelectOne;
import io.zero88.jooqx.datatype.SQLDataTypeRegistry;

import lombok.NonNull;

/**
 * Select exists result adapter that defines output in {@code Boolean} type
 *
 * @see SelectAdhocResultAdapter
 * @see SelectOne
 * @since 1.0.0
 */
public final class SelectExistsAdapter<RS, C extends SQLResultCollector<RS>>
    extends SelectAdhocResultAdapter<RS, C, TableLike<Record1<Integer>>, Boolean> implements SelectOne {

    protected SelectExistsAdapter(@NonNull TableLike<Record1<Integer>> table, @NonNull C converter) {
        super(table, converter);
    }

    @Override
    public Boolean collect(@NonNull RS resultSet, @NonNull SQLDataTypeRegistry registry) {
        return converter().collect(resultSet, converterStrategy(registry)).stream().findFirst().isPresent();
    }

    public static <RS, C extends SQLResultCollector<RS>> SelectExistsAdapter<RS, C> exist(
        @NonNull TableLike<Record1<Integer>> table, @NonNull C converter) {
        return new SelectExistsAdapter<>(table, converter);
    }

}
