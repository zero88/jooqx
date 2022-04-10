package io.github.zero88.jooqx.adapter;

import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.TableLike;

import io.github.zero88.jooqx.SQLResultCollector;
import io.github.zero88.jooqx.adapter.SQLResultAdapter.SQLResultListAdapter;
import io.github.zero88.jooqx.datatype.DataTypeMapperRegistry;

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

    public SelectList(@NotNull T table, @NotNull SQLCollectorPart<R, I> collectorPart) {
        super(table, collectorPart);
    }

    @Override
    public <RS> @NotNull List<I> collect(@NotNull RS resultSet, @NotNull SQLResultCollector<RS> collector,
                                         @NotNull DSLContext dsl, @NotNull DataTypeMapperRegistry registry) {
        return collector.collect(resultSet, createStrategy(registry, dsl));
    }

}
