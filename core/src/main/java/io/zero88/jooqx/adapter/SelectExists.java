package io.zero88.jooqx.adapter;

import java.util.Objects;

import org.jetbrains.annotations.NotNull;
import org.jooq.DSLContext;
import org.jooq.Record1;
import org.jooq.TableLike;

import io.zero88.jooqx.SQLResultCollector;
import io.zero88.jooqx.datatype.DataTypeMapperRegistry;

/**
 * Select exists result adapter that defines output in {@code Boolean} type
 *
 * @see SelectAdhocOneResult
 * @see SQLResultAdapter.SQLResultOneAdapter
 * @since 1.0.0
 */
public final class SelectExists extends SelectAdhocOneResult<TableLike<Record1<Integer>>, Boolean> {

    public SelectExists(@NotNull TableLike<Record1<Integer>> table) {
        super(table);
    }

    @Override
    public <RS> @NotNull Boolean collect(@NotNull RS resultSet, @NotNull SQLResultCollector<RS> collector, @NotNull DSLContext dsl,
                                @NotNull DataTypeMapperRegistry registry) {
        return collector.collect(resultSet, initStrategy(dsl, registry,
                                                         SQLResultAdapter.byTable(table()).andThen(Objects::nonNull)))
                        .stream()
                        .findFirst()
                        .isPresent();
    }

}
