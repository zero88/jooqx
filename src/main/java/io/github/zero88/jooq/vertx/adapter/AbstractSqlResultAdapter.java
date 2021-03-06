package io.github.zero88.jooq.vertx.adapter;

import org.jooq.Record;
import org.jooq.TableLike;

import io.github.zero88.jooq.vertx.converter.ResultSetConverter;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class AbstractSqlResultAdapter<RS, T extends TableLike<? extends Record>, R>
    implements SqlResultAdapter<RS, T, R> {

    private final ResultSetConverter<RS, T> converter;

    @Override
    @NonNull
    public final ResultSetConverter<RS, T> converter() {
        return converter;
    }

}
