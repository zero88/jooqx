package io.zero88.jooqx.adapter;

import java.util.Objects;

import org.jooq.DSLContext;
import org.jooq.Record1;
import org.jooq.TableLike;

import io.zero88.jooqx.JsonRecord;
import io.zero88.jooqx.SQLResultCollector;
import io.zero88.jooqx.datatype.DataTypeMapperRegistry;

import lombok.NonNull;

/**
 * Select count result adapter that defines output in {@code Integer} type
 *
 * @see SelectAdhocOneResult
 * @see SQLResultAdapter.SQLResultOneAdapter
 * @since 1.0.0
 */
public final class SelectCount extends SelectAdhocOneResult<TableLike<Record1<Integer>>, Integer> {

    public SelectCount(@NonNull TableLike<Record1<Integer>> table) {
        super(table);
    }

    @Override
    public <RS> Integer collect(RS resultSet, @NonNull SQLResultCollector<RS> collector, @NonNull DSLContext dsl,
                                @NonNull DataTypeMapperRegistry registry) {
        final SQLCollectorPart<JsonRecord<?>, Integer> part = SQLResultAdapter.byJson()
                                                                              .andThen(r -> r.get(0, Integer.class));
        return collector.collect(resultSet, initStrategy(dsl, registry, part))
                        .stream()
                        .filter(Objects::nonNull)
                        .findFirst()
                        .orElse(0);
    }

}
