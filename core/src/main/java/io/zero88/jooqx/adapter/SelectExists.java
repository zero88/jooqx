package io.zero88.jooqx.adapter;

import java.util.Objects;

import org.jooq.DSLContext;
import org.jooq.Record1;
import org.jooq.TableLike;

import io.zero88.jooqx.SQLResultCollector;
import io.zero88.jooqx.datatype.DataTypeMapperRegistry;

import lombok.NonNull;

/**
 * Select exists result adapter that defines output in {@code Boolean} type
 *
 * @see SelectAdhocOneResultAdapter
 * @see SelectOneStrategy
 * @since 1.0.0
 */
public final class SelectExists extends SelectAdhocOneResultAdapter<TableLike<Record1<Integer>>, Boolean> {

    public SelectExists(@NonNull TableLike<Record1<Integer>> table) {
        super(table);
    }

    @Override
    public <RS> Boolean collect(RS resultSet, @NonNull SQLResultCollector<RS> collector, @NonNull DSLContext dsl,
                                @NonNull DataTypeMapperRegistry registry) {
        return collector.collect(resultSet, initStrategy(dsl, registry,
                                                         SQLResultAdapter.byTable(table()).andThen(Objects::nonNull)))
                        .stream()
                        .findFirst()
                        .isPresent();
    }

}
