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
 * @param <T>   Type of jOOQ table like
 * @param <REC> Type of jOOQ record
 * @param <R>   Type of output result
 * @see SQLResultOneAdapter
 * @since 1.0.0
 */
public final class SelectOne<T extends TableLike<? extends Record>, REC extends Record, R>
    extends SQLResultAdapterImpl.SelectResultInternal<T, REC, R, R> implements SQLResultOneAdapter<T, R> {

    public SelectOne(@NotNull T table, @NotNull SQLCollectorPart<REC, R> collectorFactory) {
        super(table, collectorFactory);
    }

    @Override
    public <RS> @Nullable R collect(@NotNull RS resultSet, @NotNull SQLResultCollector<RS> collector,
                                    @NotNull DSLContext dsl, @NotNull DataTypeMapperRegistry registry) {
        return collector.collect(resultSet, createStrategy(registry, dsl)).stream().findFirst().orElse(null);
    }

}
