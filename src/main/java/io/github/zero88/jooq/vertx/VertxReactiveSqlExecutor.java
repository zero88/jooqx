package io.github.zero88.jooq.vertx;

import java.util.List;
import java.util.function.Function;

import org.jooq.Query;
import org.jooq.Record;
import org.jooq.Table;
import org.jooq.TableLike;

import io.github.zero88.jooq.vertx.converter.ReactiveBindParamConverter;
import io.github.zero88.jooq.vertx.converter.ResultBatchConverter;
import io.github.zero88.jooq.vertx.converter.ResultSetConverter;
import io.github.zero88.jooq.vertx.record.VertxJooqRecord;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import io.vertx.sqlclient.SqlClient;
import io.vertx.sqlclient.Tuple;

import lombok.Builder.Default;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

/**
 * Represents for an executor that executes {@code jOOQ query} on {@code Vertx reactive SQL client} connection
 *
 * @see SqlClient
 * @see Tuple
 * @see Row
 * @see RowSet
 * @since 1.0.0
 */
@Getter
@SuperBuilder
@Accessors(fluent = true)
public final class VertxReactiveSqlExecutor
    extends AbstractVertxJooqExecutor<SqlClient, Tuple, RowSet<Row>, BatchReturningResult<VertxJooqRecord<?>>>
    implements VertxReactiveBatchExecutor {

    @NonNull
    @Default
    private final QueryHelper<Tuple> helper = new QueryHelper<>(new ReactiveBindParamConverter());

    @Override
    protected <Q extends Query, R> void doExecute(@NonNull Q query, @NonNull Function<RowSet<Row>, List<R>> converter,
                                                  @NonNull Handler<AsyncResult<List<R>>> handler) {
        sqlClient().preparedQuery(helper().toPreparedQuery(dsl().configuration(), query))
                   .execute(helper().toBindValues(query), ar -> handler.handle(ar.map(converter)));
    }

    @Override
    public <Q extends Query, T extends TableLike<?>> void batchExecute(@NonNull Q query,
                                                                       @NonNull BindBatchValues bindBatchValues,
                                                                       @NonNull ResultBatchConverter<RowSet<Row>, T> rsConverter,
                                                                       @NonNull Handler<AsyncResult<BatchReturningResult<VertxJooqRecord<?>>>> handler) {
        doBatchExecute(query, bindBatchValues, rsConverter::convert, handler);
    }

    @Override
    public <Q extends Query, T extends TableLike<?>, R extends Record> void batchExecute(@NonNull Q query,
                                                                                         @NonNull BindBatchValues bindBatchValues,
                                                                                         @NonNull ResultBatchConverter<RowSet<Row>, T> rsConverter,
                                                                                         @NonNull Table<R> table,
                                                                                         @NonNull Handler<AsyncResult<BatchReturningResult<R>>> handler) {
        doBatchExecute(query, bindBatchValues, rs -> rsConverter.convert(rs, table), handler);
    }

    @Override
    public <Q extends Query, T extends TableLike<?>, R extends Record> void batchExecute(@NonNull Q query,
                                                                                         @NonNull BindBatchValues bindBatchValues,
                                                                                         @NonNull ResultSetConverter<RowSet<Row>, T> rsConverter,
                                                                                         @NonNull R record,
                                                                                         @NonNull Handler<AsyncResult<BatchReturningResult<R>>> handler) {
        doBatchExecute(query, bindBatchValues, rs -> rsConverter.convert(rs, record), handler);
    }

    @Override
    public <Q extends Query, T extends TableLike<?>, R> void batchExecute(@NonNull Q query,
                                                                          @NonNull BindBatchValues bindBatchValues,
                                                                          @NonNull ResultSetConverter<RowSet<Row>, T> rsConverter,
                                                                          @NonNull Class<R> recordClass,
                                                                          @NonNull Handler<AsyncResult<BatchReturningResult<R>>> handler) {
        doBatchExecute(query, bindBatchValues, rs -> rsConverter.convert(rs, recordClass), handler);
    }

    private <Q extends Query, R> void doBatchExecute(@NonNull Q q, @NonNull BindBatchValues b,
                                                     @NonNull Function<RowSet<Row>, List<R>> cf,
                                                     @NonNull Handler<AsyncResult<BatchReturningResult<R>>> h) {
        sqlClient().preparedQuery(helper().toPreparedQuery(dsl().configuration(), q))
                   .executeBatch(helper().toBindValues(q, b),
                                 ar -> h.handle(ar.map(cf).map(rs -> new BatchReturningResult<>(b.size(), rs))));
    }

}
