package io.github.zero88.jooq.vertx;

import java.util.List;
import java.util.function.Function;

import org.jooq.DSLContext;
import org.jooq.Query;
import org.jooq.Record;
import org.jooq.Table;
import org.jooq.TableLike;

import io.github.zero88.jooq.vertx.converter.ResultSetConverter;
import io.github.zero88.jooq.vertx.record.VertxJooqRecord;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;

import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@Accessors(fluent = true)
public abstract class AbstractVertxJooqExecutor<S, P, RS> implements VertxJooqExecutor<S, P, RS> {

    @NonNull
    private final Vertx vertx;
    @NonNull
    private final DSLContext dsl;
    @NonNull
    private final S sqlClient;

    @Override
    public <Q extends Query, T extends TableLike<?>> void execute(@NonNull Q query,
                                                                  @NonNull ResultSetConverter<RS, T> rsConverter,
                                                                  @NonNull Handler<AsyncResult<List<VertxJooqRecord<?>>>> handler) {
        doExecute(query, rsConverter::convert, handler);
    }

    @Override
    public <Q extends Query, T extends TableLike<?>, R extends Record> void execute(@NonNull Q query,
                                                                                    @NonNull ResultSetConverter<RS,
                                                                                                                   T> rsConverter,
                                                                                    @NonNull Table<R> table,
                                                                                    @NonNull Handler<AsyncResult<List<R>>> handler) {
        doExecute(query, rs -> rsConverter.convert(rs, table), handler);
    }

    @Override
    public <Q extends Query, T extends TableLike<?>, R> void execute(@NonNull Q query,
                                                                     @NonNull ResultSetConverter<RS, T> rsConverter,
                                                                     @NonNull Class<R> recordClass,
                                                                     @NonNull Handler<AsyncResult<List<R>>> handler) {
        doExecute(query, rs -> rsConverter.convert(rs, recordClass), handler);
    }

    protected abstract <Q extends Query, R> void doExecute(@NonNull Q query, @NonNull Function<RS, List<R>> converter,
                                                           @NonNull Handler<AsyncResult<List<R>>> handler);

}
