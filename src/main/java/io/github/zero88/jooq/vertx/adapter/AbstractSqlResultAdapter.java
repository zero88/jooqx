package io.github.zero88.jooq.vertx.adapter;

import org.jooq.Record;
import org.jooq.TableLike;

import io.github.zero88.jooq.vertx.converter.ResultSetConverter;

import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;

public abstract class AbstractSqlResultAdapter<RS, T extends TableLike<? extends Record>, R>
    implements SqlResultAdapter<RS, T, R> {

    @NonNull
    @Getter
    @Accessors(fluent = true)
    private final T table;
    @NonNull
    private final ResultSetConverter<RS> converter;

    protected AbstractSqlResultAdapter(@NonNull T table, @NonNull ResultSetConverter<RS> converter) {
        this.table = table;
        this.converter = converter;
    }

    @Override
    @NonNull
    public final ResultSetConverter<RS> converter() {
        return converter;
    }

}
