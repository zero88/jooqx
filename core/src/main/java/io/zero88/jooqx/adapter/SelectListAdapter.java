package io.zero88.jooqx.adapter;

import java.util.List;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.TableLike;

import io.zero88.jooqx.SQLResultCollector;
import io.zero88.jooqx.adapter.SQLResultAdapter.SelectMany;
import io.zero88.jooqx.datatype.SQLDataTypeRegistry;

import lombok.NonNull;

/**
 * Select list result adapter that returns list of output
 *
 * @see SQLResultAdapter
 * @see SelectMany
 * @since 1.0.0
 */
public final class SelectListAdapter<RS, C extends SQLResultCollector<RS>, T extends TableLike<? extends Record>,
                                        R extends Record, O>
    extends SQLResultAdapterImpl.SelectResultInternal<RS, C, T, R, O, List<O>> implements SelectMany {

    public SelectListAdapter(@NonNull T table, @NonNull C converter, @NonNull CollectorPart<R, O> collectorPart) {
        super(table, converter, collectorPart);
    }

    @Override
    public List<O> collect(@NonNull RS resultSet, @NonNull DSLContext dsl, @NonNull SQLDataTypeRegistry registry) {
        return converter().collect(resultSet, createStrategy(registry, dsl));
    }

}
