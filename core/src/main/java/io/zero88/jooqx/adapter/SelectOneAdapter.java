package io.zero88.jooqx.adapter;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.TableLike;

import io.zero88.jooqx.SQLResultCollector;
import io.zero88.jooqx.adapter.SQLResultAdapter.SelectOne;
import io.zero88.jooqx.datatype.SQLDataTypeRegistry;

import lombok.NonNull;

/**
 * Select one result adapter that returns only one row
 *
 * @see SQLResultAdapter
 * @see SelectOne
 * @since 1.0.0
 */
public final class SelectOneAdapter<RS, C extends SQLResultCollector<RS>, T extends TableLike<? extends Record>,
                                       R extends Record, O>
    extends SQLResultAdapterImpl.SelectResultInternal<RS, C, T, R, O, O> implements SelectOne {

    public SelectOneAdapter(@NonNull T table, @NonNull C converter, @NonNull CollectorPart<R, O> collectorPart) {
        super(table, converter, collectorPart);
    }

    @Override
    public O collect(@NonNull RS resultSet, @NonNull DSLContext dsl, @NonNull SQLDataTypeRegistry registry) {
        return converter().collect(resultSet, createStrategy(registry, dsl)).stream().findFirst().orElse(null);
    }

    public static <RS, C extends SQLResultCollector<RS>, T extends TableLike<? extends Record>, R extends Record, O> SelectOneAdapter<RS, C, T, R, O> create(
        @NonNull T table, @NonNull C converter, @NonNull CollectorPart<R, O> collectorPart) {
        return new SelectOneAdapter<>(table, converter, collectorPart);
    }

}
