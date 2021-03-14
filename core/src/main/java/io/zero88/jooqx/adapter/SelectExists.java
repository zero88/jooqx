package io.zero88.jooqx.adapter;

import java.util.Objects;

import org.jooq.DSLContext;
import org.jooq.Record1;
import org.jooq.TableLike;

import io.zero88.jooqx.SQLResultCollector;
import io.zero88.jooqx.datatype.SQLDataTypeRegistry;

import lombok.NonNull;

/**
 * Select exists result adapter that defines output in {@code Boolean} type
 *
 * @see SelectAdhocOneResultAdapter
 * @see SelectOneStrategy
 * @since 1.0.0
 */
public final class SelectExists<RS, C extends SQLResultCollector<RS>>
    extends SelectAdhocOneResultAdapter<RS, C, TableLike<Record1<Integer>>, Boolean> {

    private SelectExists(@NonNull TableLike<Record1<Integer>> table, @NonNull C converter) {
        super(table, converter);
    }

    @Override
    public Boolean collect(@NonNull RS resultSet, @NonNull DSLContext dsl, @NonNull SQLDataTypeRegistry registry) {
        return converter().collect(resultSet, initStrategy(dsl, registry,
                                                           SQLResultAdapter.byTable(table()).andThen(Objects::nonNull)))
                          .stream()
                          .findFirst()
                          .isPresent();
    }

    public static <RS, C extends SQLResultCollector<RS>> SelectExists<RS, C> exist(
        @NonNull TableLike<Record1<Integer>> table, @NonNull C converter) {
        return new SelectExists<>(table, converter);
    }

}
