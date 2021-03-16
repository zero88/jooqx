package io.zero88.jooqx.adapter;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.TableLike;

import io.zero88.jooqx.SQLResultCollector;
import io.zero88.jooqx.adapter.SQLResultAdapter.SQLResultOneAdapter;
import io.zero88.jooqx.datatype.DataTypeMapperRegistry;

import lombok.NonNull;

/**
 * Select one result adapter that returns only one row
 *
 * @param <T> Type of jOOQ table like
 * @param <R> Type of jOOQ record
 * @param <I> Type of output
 * @see SQLResultOneAdapter
 * @since 1.0.0
 */
public final class SelectOne<T extends TableLike<? extends Record>, R extends Record, I>
    extends SQLResultAdapterImpl.SelectResultInternal<T, R, I, I> implements SQLResultOneAdapter<T, I> {

    public SelectOne(@NonNull T table, @NonNull SQLCollectorPart<R, I> collectorPart) {
        super(table, collectorPart);
    }

    @Override
    public <RS> I collect(RS resultSet, @NonNull SQLResultCollector<RS> collector, @NonNull DSLContext dsl,
                          @NonNull DataTypeMapperRegistry registry) {
        return collector.collect(resultSet, createStrategy(registry, dsl)).stream().findFirst().orElse(null);
    }

}
