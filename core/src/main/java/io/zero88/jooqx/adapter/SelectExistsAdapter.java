package io.zero88.jooqx.adapter;

import java.util.function.BiFunction;

import org.jooq.Record1;
import org.jooq.TableLike;

import io.zero88.jooqx.SQLResultConverter;

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

    protected SelectExistsAdapter(@NonNull TableLike<Record1<Integer>> table, @NonNull C converter,
                                  @NonNull BiFunction<SQLResultAdapter<R, C, TableLike<Record1<Integer>>, Boolean>, R
                                                         , Boolean> function) {
        super(table, converter, function);
    }

    public static <RS, C extends SQLResultConverter<RS>> SelectExistsAdapter<RS, C> exist(
        @NonNull TableLike<Record1<Integer>> table, @NonNull C converter) {
        return new SelectExistsAdapter<>(table, converter, (a, rs) -> a.converter()
                                                                       .convertJsonRecord(rs, a.table())
                                                                       .stream()
                                                                       .findFirst()
                                                                       .isPresent());
    }

}
