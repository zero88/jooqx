package io.zero88.jooqx.adapter;

import org.jooq.Record1;
import org.jooq.TableLike;

import io.zero88.jooqx.SQLResultConverter;
import io.zero88.jooqx.datatype.SQLDataTypeRegistry;

import lombok.NonNull;

/**
 * Select exists result adapter that defines output in {@code Boolean} type
 *
 * @param <R> Type of SQL result set
 * @param <C> Type of SQL result set converter
 * @since 1.0.0
 */
public final class SelectExistsAdapter<R, C extends SQLResultConverter<R>>
    extends SelectAdhocOneResultAdapter<R, C, TableLike<Record1<Integer>>, Boolean> {

    protected SelectExistsAdapter(@NonNull TableLike<Record1<Integer>> table, @NonNull C converter) {
        super(table, converter);
    }

    @Override
    public Boolean collect(@NonNull R resultSet, @NonNull SQLDataTypeRegistry registry) {
        return converter().collect(resultSet, createConverterStrategy(registry)).stream().findFirst().isPresent();
    }

    public static <RS, C extends SQLResultConverter<RS>> SelectExistsAdapter<RS, C> exist(
        @NonNull TableLike<Record1<Integer>> table, @NonNull C converter) {
        return new SelectExistsAdapter<>(table, converter);
    }

}
