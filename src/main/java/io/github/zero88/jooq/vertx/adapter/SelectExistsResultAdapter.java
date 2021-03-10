package io.github.zero88.jooq.vertx.adapter;

import java.util.function.BiFunction;

import org.jooq.Record1;
import org.jooq.TableLike;

import io.github.zero88.jooq.vertx.converter.ResultSetConverter;

import lombok.NonNull;

public final class SelectExistsResultAdapter<RS, C extends ResultSetConverter<RS>>
    extends SelectAdhocOneResultAdapter<RS, C, TableLike<Record1<Integer>>, Boolean> {

    protected SelectExistsResultAdapter(@NonNull TableLike<Record1<Integer>> table, @NonNull C converter,
                                        @NonNull BiFunction<SQLResultAdapter<RS, C, TableLike<Record1<Integer>>,
                                                                                                                        Boolean>, RS, Boolean> function) {
        super(table, converter, function);
    }

    public static <RS, C extends ResultSetConverter<RS>> SelectExistsResultAdapter<RS, C> exist(
        @NonNull TableLike<Record1<Integer>> table, @NonNull C converter) {
        return new SelectExistsResultAdapter<>(table, converter, (a, rs) -> a.converter()
                                                                             .convertJsonRecord(rs, a.table())
                                                                             .stream()
                                                                             .findFirst()
                                                                             .isPresent());
    }

}
