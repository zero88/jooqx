package io.github.zero88.jooq.vertx.adapter;

import org.jooq.Record;
import org.jooq.TableLike;

import io.github.zero88.jooq.vertx.converter.ResultSetConverter;

import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;

public abstract class AbstractSqlResultAdapter<RS, C extends ResultSetConverter<RS>, T extends TableLike<? extends Record>, R>
    implements SqlResultAdapter<RS, C, T, R> {

    @NonNull
    @Getter
    @Accessors(fluent = true)
    private final T table;
    @NonNull
    private final C converter;

    @SuppressWarnings("unchecked")
    protected AbstractSqlResultAdapter(@NonNull T table, @NonNull C converter) {
        this.table = table;
        this.converter = (C) converter.setup(strategy());
    }

    @Override
    @NonNull
    public final C converter() {
        return converter;
    }

}
