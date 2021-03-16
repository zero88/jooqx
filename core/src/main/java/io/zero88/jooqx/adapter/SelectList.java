package io.zero88.jooqx.adapter;

import java.util.List;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.TableLike;

import io.zero88.jooqx.SQLResultCollector;
import io.zero88.jooqx.adapter.SQLResultAdapter.SelectManyStrategy;
import io.zero88.jooqx.datatype.DataTypeMapperRegistry;

import lombok.NonNull;

/**
 * Select list result adapter that returns list of output
 *
 * @see SQLResultAdapter
 * @see SelectManyStrategy
 * @since 1.0.0
 */
public final class SelectList<T extends TableLike<? extends Record>, R extends Record, O>
    extends SQLResultAdapterImpl.SelectResultInternal<T, R, O, List<O>> implements SelectManyStrategy {

    public SelectList(@NonNull T table, @NonNull SQLCollectorPart<R, O> collectorPart) {
        super(table, collectorPart);
    }

    @Override
    public <RS> List<O> collect(@NonNull RS resultSet, @NonNull SQLResultCollector<RS> collector,
                                @NonNull DSLContext dsl, @NonNull DataTypeMapperRegistry registry) {
        return collector.collect(resultSet, createStrategy(registry, dsl));
    }

}
