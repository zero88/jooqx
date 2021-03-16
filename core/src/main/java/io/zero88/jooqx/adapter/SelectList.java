package io.zero88.jooqx.adapter;

import java.util.List;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.TableLike;

import io.zero88.jooqx.SQLResultCollector;
import io.zero88.jooqx.adapter.SQLResultAdapter.SQLResultListAdapter;
import io.zero88.jooqx.datatype.DataTypeMapperRegistry;

import lombok.NonNull;

/**
 * Select list result adapter that returns list of output
 *
 * @param <T> Type of jOOQ table like
 * @param <R> Type of jOOQ record
 * @param <I> Type of each item in output
 * @see SQLResultListAdapter
 * @since 1.0.0
 */
public final class SelectList<T extends TableLike<? extends Record>, R extends Record, I>
    extends SQLResultAdapterImpl.SelectResultInternal<T, R, I, List<I>> implements SQLResultListAdapter<T, I> {

    public SelectList(@NonNull T table, @NonNull SQLCollectorPart<R, I> collectorPart) {
        super(table, collectorPart);
    }

    @Override
    public <RS> List<I> collect(@NonNull RS resultSet, @NonNull SQLResultCollector<RS> collector,
                                @NonNull DSLContext dsl, @NonNull DataTypeMapperRegistry registry) {
        return collector.collect(resultSet, createStrategy(registry, dsl));
    }

}
