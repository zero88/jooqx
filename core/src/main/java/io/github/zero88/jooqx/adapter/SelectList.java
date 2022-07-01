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
 * @param <T>   Type of jOOQ table like
 * @param <REC> Type of jOOQ record
 * @param <R>   Type of each item in list result
 * @see SQLResultListAdapter
 * @since 1.0.0
 */
public final class SelectList<T extends TableLike<? extends Record>, REC extends Record, R>
    extends SQLResultAdapterImpl.SelectResultInternal<T, REC, R, List<R>> implements SQLResultListAdapter<T, R> {

    public SelectList(@NotNull T table, @NotNull SQLCollectorPart<REC, R> collectorFactory) {
        super(table, collectorFactory);
    }

    @Override
    public <RS> @NotNull List<R> collect(@NotNull RS resultSet, @NotNull SQLResultCollector<RS> collector,
                                         @NotNull DSLContext dsl, @NotNull DataTypeMapperRegistry registry) {
        return collector.collect(resultSet, createStrategy(registry, dsl));
    }

}
