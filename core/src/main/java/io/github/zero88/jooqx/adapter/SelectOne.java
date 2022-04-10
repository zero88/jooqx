package io.github.zero88.jooqx.adapter;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.TableLike;

import io.github.zero88.jooqx.SQLResultCollector;
import io.github.zero88.jooqx.adapter.SQLResultAdapter.SQLResultOneAdapter;
import io.github.zero88.jooqx.datatype.DataTypeMapperRegistry;

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

    public SelectOne(@NotNull T table, @NotNull SQLCollectorPart<R, I> collectorPart) {
        super(table, collectorPart);
    }

    @Override
    public <RS> @Nullable I collect(@NotNull RS resultSet, @NotNull SQLResultCollector<RS> collector,
                                    @NotNull DSLContext dsl, @NotNull DataTypeMapperRegistry registry) {
        return collector.collect(resultSet, createStrategy(registry, dsl)).stream().findFirst().orElse(null);
    }

}
