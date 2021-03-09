package io.github.zero88.jooq.vertx.adapter;

import java.util.function.BiFunction;

import org.jooq.Record;
import org.jooq.TableLike;

import io.github.zero88.jooq.vertx.adapter.HasStrategy.SelectOne;
import io.github.zero88.jooq.vertx.converter.ResultSetConverter;

import lombok.NonNull;

public abstract class SelectAdhocOneResultAdapter<RS, C extends ResultSetConverter<RS>, T extends TableLike<? extends Record>, R>
    extends AbstractSqlResultAdapter<RS, C, T, R> implements SelectOne {

    private final BiFunction<SqlResultAdapter<RS, C, T, R>, RS, R> function;

    protected SelectAdhocOneResultAdapter(@NonNull T table, @NonNull C converter,
                                          BiFunction<SqlResultAdapter<RS, C, T, R>, RS, R> function) {
        super(table, converter);
        this.function = function;
    }

    @Override
    public @NonNull R convert(@NonNull RS resultSet) {
        return function.apply(this, resultSet);
    }

}
