package io.zero88.jooqx.adapter;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.TableLike;

import io.zero88.jooqx.SQLResultCollector;
import io.zero88.jooqx.adapter.SQLResultAdapter.SelectOneStrategy;
import io.zero88.jooqx.datatype.DataTypeMapperRegistry;

import lombok.NonNull;

/**
 * Select one result adapter that returns only one row
 *
 * @see SQLResultAdapter
 * @see SelectOneStrategy
 * @since 1.0.0
 */
public final class SelectOne<T extends TableLike<? extends Record>, R extends Record, O>
    extends SQLResultAdapterImpl.SelectResultInternal<T, R, O, O> implements SelectOneStrategy {

    public SelectOne(@NonNull T table, @NonNull SQLCollectorPart<R, O> collectorPart) {
        super(table, collectorPart);
    }

    @Override
    public <RS> O collect(RS resultSet, @NonNull SQLResultCollector<RS> collector, @NonNull DSLContext dsl,
                          @NonNull DataTypeMapperRegistry registry) {
        return collector.collect(resultSet, createStrategy(registry, dsl)).stream().findFirst().orElse(null);
    }

    public static <T extends TableLike<? extends Record>, R extends Record, O> SelectOne<T, R, O> create(
        @NonNull T table, @NonNull SQLCollectorPart<R, O> collectorPart) {
        return new SelectOne<>(table, collectorPart);
    }

}
