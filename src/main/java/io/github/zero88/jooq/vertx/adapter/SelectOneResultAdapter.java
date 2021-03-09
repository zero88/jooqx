package io.github.zero88.jooq.vertx.adapter;

import java.util.function.BiFunction;

import org.jooq.Record;
import org.jooq.TableLike;

import io.github.zero88.jooq.vertx.VertxJooqRecord;
import io.github.zero88.jooq.vertx.converter.ResultSetConverter;

import lombok.NonNull;

public class SelectOneResultAdapter<RS, C extends ResultSetConverter<RS>, T extends TableLike<? extends Record>, R>
    extends AbstractSqlResultAdapter<RS, C, T, R> {

    private final BiFunction<SqlResultAdapter<RS, C, T, R>, RS, R> function;

    protected SelectOneResultAdapter(@NonNull T table, @NonNull C converter,
                                     @NonNull BiFunction<SqlResultAdapter<RS, C, T, R>, RS, R> function) {
        super(table, converter);
        this.function = function;
    }

    @Override
    public final @NonNull SelectStrategy strategy() {
        return SelectStrategy.FIRST_ONE;
    }

    @Override
    public @NonNull R convert(@NonNull RS resultSet) {
        return function.apply(this, resultSet);
    }

    public static <RS, C extends ResultSetConverter<RS>, T extends TableLike<? extends Record>> SelectOneResultAdapter<RS, C, T, VertxJooqRecord<?>> vertxRecord(
        @NonNull T table, @NonNull C converter) {
        return new SelectOneResultAdapter<>(table, converter, (a, rs) -> a.converter()
                                                                          .convertVertxRecord(rs, a.table())
                                                                          .stream()
                                                                          .findFirst()
                                                                          .orElse(null));
    }

}
