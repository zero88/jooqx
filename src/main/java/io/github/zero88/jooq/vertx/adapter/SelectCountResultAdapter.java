package io.github.zero88.jooq.vertx.adapter;

import java.util.function.BiFunction;

import org.jooq.Record1;
import org.jooq.TableLike;

import io.github.zero88.jooq.vertx.SQLResultSetConverter;

import lombok.NonNull;

/**
 * Select count result adapter that defines output in {@code Integer} type
 *
 * @param <R> Type of SQL result set
 * @param <C> Type of SQL result set converter
 * @since 1.0.0
 */
public final class SelectCountResultAdapter<R, C extends SQLResultSetConverter<R>>
    extends SelectAdhocOneResultAdapter<R, C, TableLike<Record1<Integer>>, Integer> {

    protected SelectCountResultAdapter(@NonNull TableLike<Record1<Integer>> table, @NonNull C converter,
                                       @NonNull BiFunction<SQLResultAdapter<R, C, TableLike<Record1<Integer>>,
                                                                               Integer>, R, Integer> function) {
        super(table, converter, function);
    }

    public static <RS, C extends SQLResultSetConverter<RS>> SelectCountResultAdapter<RS, C> count(
        @NonNull TableLike<Record1<Integer>> table, @NonNull C converter) {
        return new SelectCountResultAdapter<>(table, converter, (a, rs) -> a.converter()
                                                                            .convertJsonRecord(rs, a.table())
                                                                            .stream()
                                                                            .findFirst()
                                                                            .map(s -> s.get(0, Integer.class))
                                                                            .orElse(0));
    }

}
