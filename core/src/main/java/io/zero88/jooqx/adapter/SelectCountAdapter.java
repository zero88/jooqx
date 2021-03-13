package io.zero88.jooqx.adapter;

import org.jooq.Record1;
import org.jooq.TableLike;

import io.zero88.jooqx.SQLResultConverter;
import io.zero88.jooqx.datatype.SQLDataTypeRegistry;

import lombok.NonNull;

/**
 * Select count result adapter that defines output in {@code Integer} type
 *
 * @param <R> Type of SQL result set
 * @param <C> Type of SQL result set converter
 * @since 1.0.0
 */
public final class SelectCountAdapter<R, C extends SQLResultConverter<R>>
    extends SelectAdhocOneResultAdapter<R, C, TableLike<Record1<Integer>>, Integer> {

    protected SelectCountAdapter(@NonNull TableLike<Record1<Integer>> table, @NonNull C converter) {
        super(table, converter);
    }

    @Override
    public Integer collect(@NonNull R resultSet, @NonNull SQLDataTypeRegistry registry) {
        return converter().collect(resultSet, createConverterStrategy(registry))
                          .stream()
                          .findFirst()
                          .map(r -> r.get(0, Integer.class))
                          .orElse(0);
    }

    public static <RS, C extends SQLResultConverter<RS>> SelectCountAdapter<RS, C> count(
        @NonNull TableLike<Record1<Integer>> table, @NonNull C converter) {
        return new SelectCountAdapter<>(table, converter);
    }

}
