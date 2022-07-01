package io.github.zero88.jooqx.adapter;

import java.util.Objects;

import org.jetbrains.annotations.NotNull;
import org.jooq.DSLContext;
import org.jooq.Record1;
import org.jooq.TableLike;

import io.github.zero88.jooqx.SQLResultCollector;
import io.github.zero88.jooqx.datatype.DataTypeMapperRegistry;

/**
 * Select count result adapter that defines output in {@code Integer} type
 *
 * @see SelectAdhocOneResult
 * @see SQLResultAdapter.SQLResultOneAdapter
 * @since 1.0.0
 */
public final class SelectCount extends SelectAdhocOneResult<TableLike<Record1<Integer>>, Integer> {

    public SelectCount(@NotNull TableLike<Record1<Integer>> table) {
        super(table);
    }

    @Override
    public @NotNull <RS> Integer collect(@NotNull RS resultSet, @NotNull SQLResultCollector<RS> collector,
                                         @NotNull DSLContext dsl, @NotNull DataTypeMapperRegistry registry) {
        return collector.collect(resultSet, initStrategy(dsl, registry, SQLResultAdapter.byJson()
                                                                                        .andThen(r -> r.get(0,
                                                                                                            Integer.class))))
                        .stream()
                        .filter(Objects::nonNull)
                        .findFirst()
                        .orElse(0);
    }

}
