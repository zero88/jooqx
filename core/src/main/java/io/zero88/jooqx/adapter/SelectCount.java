package io.zero88.jooqx.adapter;

import org.jooq.DSLContext;
import org.jooq.Record1;
import org.jooq.TableLike;

import io.zero88.jooqx.JsonRecord;
import io.zero88.jooqx.SQLResultCollector;
import io.zero88.jooqx.datatype.SQLDataTypeRegistry;

import lombok.NonNull;

/**
 * Select count result adapter that defines output in {@code Integer} type
 *
 * @see SelectAdhocOneResultAdapter
 * @see SelectOneStrategy
 * @since 1.0.0
 */
public final class SelectCount<RS, C extends SQLResultCollector<RS>>
    extends SelectAdhocOneResultAdapter<RS, C, TableLike<Record1<Integer>>, Integer> {

    private SelectCount(@NonNull TableLike<Record1<Integer>> table, @NonNull C converter) {
        super(table, converter);
    }

    @Override
    public Integer collect(@NonNull RS resultSet, @NonNull DSLContext dsl, @NonNull SQLDataTypeRegistry registry) {
        final CollectorPart<JsonRecord<?>, Integer> part = SQLResultAdapter.byJson()
                                                                           .andThen(r -> r.get(0, Integer.class));
        return converter().collect(resultSet, initStrategy(dsl, registry, part)).stream().findFirst().orElse(0);
    }

    public static <RS, C extends SQLResultCollector<RS>> SelectCount<RS, C> count(
        @NonNull TableLike<Record1<Integer>> table, @NonNull C converter) {
        return new SelectCount<>(table, converter);
    }

}
