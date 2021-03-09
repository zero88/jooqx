package io.github.zero88.jooq.vertx.adapter;

import java.util.function.BiFunction;

import org.jooq.Record1;
import org.jooq.TableLike;

import io.github.zero88.jooq.vertx.converter.ResultSetConverter;

import lombok.NonNull;

public final class SelectCountResultAdapter<RS, C extends ResultSetConverter<RS>>
    extends SelectAdhocOneResultAdapter<RS, C, TableLike<Record1<Integer>>, Integer> {

    protected SelectCountResultAdapter(@NonNull TableLike<Record1<Integer>> table, @NonNull C converter,
                                       @NonNull BiFunction<SqlResultAdapter<RS, C, TableLike<Record1<Integer>>,
                                                                               Integer>, RS, Integer> function) {
        super(table, converter, function);
    }

    public static <RS, C extends ResultSetConverter<RS>> SelectCountResultAdapter<RS, C> count(
        @NonNull TableLike<Record1<Integer>> table, @NonNull C converter) {
        return new SelectCountResultAdapter<>(table, converter, (a, rs) -> a.converter()
                                                                            .convertVertxRecord(rs, a.table())
                                                                            .stream()
                                                                            .findFirst()
                                                                            .map(s -> s.get(0, Integer.class))
                                                                            .orElse(0));
    }

}
