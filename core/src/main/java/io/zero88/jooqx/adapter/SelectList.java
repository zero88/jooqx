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
public final class SelectList<RS, C extends SQLResultCollector<RS>, T extends TableLike<? extends Record>,
                                 R extends Record, O>
    extends SQLResultAdapterImpl.SelectResultInternal<RS, C, T, R, O, List<O>> implements SelectManyStrategy {

    public SelectList(@NonNull T table, @NonNull C converter, @NonNull SQLCollectorPart<R, O> collectorPart) {
        super(table, converter, collectorPart);
    }

    @Override
    public List<O> collect(@NonNull RS resultSet, @NonNull DSLContext dsl, @NonNull DataTypeMapperRegistry registry) {
        return converter().collect(resultSet, createStrategy(registry, dsl));
    }

}
