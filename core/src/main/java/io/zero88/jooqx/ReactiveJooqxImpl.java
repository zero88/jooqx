package io.zero88.jooqx;

import java.util.function.Function;

import org.jooq.DSLContext;
import org.jooq.Query;
import org.jooq.Record;
import org.jooq.TableLike;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.sqlclient.Pool;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import io.vertx.sqlclient.SqlClient;
import io.vertx.sqlclient.SqlConnection;
import io.vertx.sqlclient.Transaction;
import io.vertx.sqlclient.Tuple;
import io.zero88.jooqx.MiscImpl.BatchResultImpl;
import io.zero88.jooqx.ReactiveSQLImpl.ReactiveSQLBC;
import io.zero88.jooqx.ReactiveSQLImpl.ReactiveSQLPQ;
import io.zero88.jooqx.ReactiveSQLImpl.ReactiveSQLRC;
import io.zero88.jooqx.SQLImpl.SQLEI;
import io.zero88.jooqx.adapter.SQLResultAdapter;
import io.zero88.jooqx.adapter.SelectList;
import io.zero88.jooqx.datatype.DataTypeMapperRegistry;

import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;

/**
 * Represents for an executor that executes {@code jOOQ query} on {@code Vertx reactive SQL client} connection
 *
 * @param <S> Type of SqlClient, can be {@code SqlConnection} or {@code Pool}
 * @see SqlClient
 * @see SqlConnection
 * @see Pool
 * @see Tuple
 * @see Row
 * @see RowSet
 * @since 1.0.0
 */
@Getter
@Accessors(fluent = true)
final class ReactiveJooqxImpl<S extends SqlClient>
    extends SQLEI<S, Tuple, ReactiveSQLPreparedQuery, RowSet<Row>, ReactiveSQLResultCollector>
    implements ReactiveJooqx<S> {

    ReactiveJooqxImpl(Vertx vertx, DSLContext dsl, S sqlClient, ReactiveSQLPreparedQuery preparedQuery,
                      ReactiveSQLResultCollector resultCollector, SQLErrorConverter errorConverter,
                      DataTypeMapperRegistry typeMapperRegistry) {
        super(vertx, dsl, sqlClient, preparedQuery, resultCollector, errorConverter, typeMapperRegistry);
    }

    @Override
    @SuppressWarnings("unchecked")
    public @NonNull ReactiveJooqx<S> transaction() {
        return this;
    }

    @Override
    public <T extends TableLike<?>, R> Future<R> execute(@NonNull Query query,
                                                         @NonNull SQLResultAdapter<T, R> adapter) {
        return sqlClient().preparedQuery(preparedQuery().sql(dsl().configuration(), query))
                          .execute(preparedQuery().bindValues(query, typeMapperRegistry()))
                          .map(rs -> adapter.collect(rs, resultCollector(), dsl(), typeMapperRegistry()))
                          .otherwise(errorConverter()::reThrowError);
    }

    @Override
    public Future<BatchResult> batch(@NonNull Query query, @NonNull BindBatchValues bindBatchValues) {
        return sqlClient().preparedQuery(preparedQuery().sql(dsl().configuration(), query))
                          .executeBatch(preparedQuery().bindValues(query, bindBatchValues, typeMapperRegistry()))
                          .map(r -> new ReactiveSQLBC().batchResultSize(r))
                          .map(s -> BatchResultImpl.create(bindBatchValues.size(), s))
                          .otherwise(errorConverter()::reThrowError);
    }

    @Override
    public <T extends TableLike<?>, R extends Record, O> Future<BatchReturningResult<O>> batch(@NonNull Query query,
                                                                                               @NonNull BindBatchValues bindBatchValues,
                                                                                               @NonNull SelectList<T, R, O> adapter) {
        return sqlClient().preparedQuery(preparedQuery().sql(dsl().configuration(), query))
                          .executeBatch(preparedQuery().bindValues(query, bindBatchValues, typeMapperRegistry()))
                          .map(rs -> adapter.collect(rs, new ReactiveSQLBC(), dsl(), typeMapperRegistry()))
                          .map(rs -> BatchResultImpl.create(bindBatchValues.size(), rs))
                          .otherwise(errorConverter()::reThrowError);
    }

    @Override
    public <X> Future<X> run(@NonNull Function<ReactiveJooqx<S>, Future<X>> function) {
        final S c = sqlClient();
        if (c instanceof Pool) {
            return ((Pool) c).getConnection()
                             .compose(conn -> beginTx(conn, function),
                                      t -> Future.failedFuture(connFailed("Unable to open SQL connection", t)));
        }
        String msg = c instanceof SqlConnection
                     ? "Unsupported using connection on SQL connection: [" + c.getClass() + "]. Switch using SQL pool"
                     : "Unable to open transaction due to unknown SQL client: [" + c.getClass() + "]";
        return Future.failedFuture(connFailed(msg));
    }

    @Override
    protected ReactiveJooqxImpl<S> withSqlClient(@NonNull S sqlClient) {
        return (ReactiveJooqxImpl<S>) ReactiveJooqxImpl.<S>builder().vertx(vertx())
                                                                    .sqlClient(sqlClient)
                                                                    .dsl(dsl())
                                                                    .preparedQuery(preparedQuery())
                                                                    .resultCollector(resultCollector())
                                                                    .errorConverter(errorConverter())
                                                                    .typeMapperRegistry(typeMapperRegistry())
                                                                    .build();
    }

    static <S extends SqlClient> ReactiveJooqxBuilder<S> builder() {
        return new ReactiveJooqxBuilder<>();
    }

    @SuppressWarnings("unchecked")
    private <X> Future<X> beginTx(@NonNull SqlConnection conn,
                                  @NonNull Function<ReactiveJooqx<S>, Future<X>> transaction) {
        return conn.begin()
                   .flatMap(tx -> transaction.apply(withSqlClient((S) conn))
                                             .compose(res -> tx.commit().flatMap(v -> Future.succeededFuture(res)),
                                                      err -> rollbackHandler(tx, errorConverter().handle(err))))
                   .onComplete(ar -> conn.close());
    }

    private <X> Future<X> rollbackHandler(@NonNull Transaction tx, @NonNull RuntimeException t) {
        return tx.rollback().compose(v -> Future.failedFuture(t), failure -> Future.failedFuture(t));
    }

    @Override
    protected ReactiveSQLPreparedQuery defPrepareQuery() {
        return new ReactiveSQLPQ();
    }

    @Override
    protected ReactiveSQLResultCollector defResultCollector() {
        return new ReactiveSQLRC();
    }

    static class ReactiveJooqxBuilder<S extends SqlClient> extends
                                                           SQLExecutorBuilder<S, Tuple, ReactiveSQLPreparedQuery,
                                                                                 RowSet<Row>,
                                                                                 ReactiveSQLResultCollector,
                                                                                 ReactiveJooqx<S>> {

        @Override
        public ReactiveJooqx<S> build() {
            return new ReactiveJooqxImpl<>(vertx(), dsl(), sqlClient(), preparedQuery(), resultCollector(),
                                           errorConverter(), typeMapperRegistry());
        }

    }

}
