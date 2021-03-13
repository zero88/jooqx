package io.zero88.jooqx;

import java.util.function.Function;

import org.jooq.Query;
import org.jooq.TableLike;

import io.vertx.core.Future;
import io.vertx.sqlclient.Pool;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import io.vertx.sqlclient.SqlClient;
import io.vertx.sqlclient.SqlConnection;
import io.vertx.sqlclient.Transaction;
import io.vertx.sqlclient.Tuple;
import io.zero88.jooqx.MiscImpl.BatchResultImpl;
import io.zero88.jooqx.ReactiveSQLImpl.ReactiveSQLPQ;
import io.zero88.jooqx.ReactiveSQLImpl.ReactiveSQLRBC;
import io.zero88.jooqx.SQLImpl.SQLEI;
import io.zero88.jooqx.adapter.SQLResultAdapter;
import io.zero88.jooqx.adapter.SelectListResultAdapter;

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
final class ReactiveJooqxImpl<S extends SqlClient> extends SQLEI<S, Tuple, RowSet<Row>, ReactiveSQLResultConverter>
    implements ReactiveJooqx<S>, SQLTxExecutor<S, Tuple, RowSet<Row>, ReactiveSQLResultConverter, ReactiveJooqx<S>>,
               ReactiveSQLBatchExecutor {

    @NonNull
    private ReactiveSQLPreparedQuery preparedQuery = new ReactiveSQLPQ();

    protected ReactiveJooqxImpl(ReactiveJooqxImplBuilder<S, ?, ?> b) {
        super(b);
        this.preparedQuery = b.preparedQuery$value;
    }

    public static <S extends SqlClient> ReactiveJooqxImplBuilder<S, ?, ?> builder() {return new ReactiveJooqxImplBuilderImpl<S>();}

    @Override
    public <T extends TableLike<?>, R> Future<R> execute(@NonNull Query query,
                                                         @NonNull SQLResultAdapter<RowSet<Row>,
                                                                                      ReactiveSQLResultConverter, T,
                                                                                      R> resultAdapter) {
        return sqlClient().preparedQuery(preparedQuery().sql(dsl().configuration(), query))
                          .execute(preparedQuery().bindValues(query))
                          .map(resultAdapter::convert)
                          .otherwise(errorConverter()::reThrowError);
    }

    @Override
    @SuppressWarnings("unchecked")
    public @NonNull ReactiveJooqx<S> transaction() {
        return this;
    }

    @Override
    public Future<BatchResult> batch(@NonNull Query query, @NonNull BindBatchValues bindBatchValues) {
        return sqlClient().preparedQuery(preparedQuery().sql(dsl().configuration(), query))
                          .executeBatch(preparedQuery().bindValues(query, bindBatchValues))
                          .map(r -> new ReactiveSQLRBC().batchResultSize(r))
                          .map(s -> BatchResultImpl.create(bindBatchValues.size(), s))
                          .otherwise(errorConverter()::reThrowError);
    }

    @Override
    public <T extends TableLike<?>, R> Future<BatchReturningResult<R>> batch(@NonNull Query query,
                                                                             @NonNull BindBatchValues bindBatchValues,
                                                                             @NonNull SelectListResultAdapter<RowSet<Row>, ReactiveSQLBatchConverter, T, R> adapter) {
        return sqlClient().preparedQuery(preparedQuery().sql(dsl().configuration(), query))
                          .executeBatch(preparedQuery().bindValues(query, bindBatchValues))
                          .map(adapter::convert)
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
        return ReactiveJooqxImpl.<S>builder().vertx(vertx())
                                             .sqlClient(sqlClient)
                                             .dsl(dsl())
                                             .preparedQuery(preparedQuery())
                                             .errorConverter(errorConverter())
                                             .build();
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

    public static abstract class ReactiveJooqxImplBuilder<S extends SqlClient, C extends ReactiveJooqxImpl<S>,
                                                             B extends ReactiveJooqxImplBuilder<S, C, B>>
        extends SQLEIBuilder<S, Tuple, RowSet<Row>, ReactiveSQLResultConverter, C, B> {

        private @NonNull ReactiveSQLPreparedQuery preparedQuery$value;
        private boolean preparedQuery$set;

        public B preparedQuery(@NonNull ReactiveSQLPreparedQuery preparedQuery) {
            this.preparedQuery$value = preparedQuery;
            this.preparedQuery$set = true;
            return self();
        }

        protected abstract B self();

        public abstract C build();

        public String toString() {
            return "ReactiveJooqxImpl.ReactiveJooqxImplBuilder(super=" + super.toString() + ", preparedQuery$value=" +
                   this.preparedQuery$value + ")";
        }

    }


    private static final class ReactiveJooqxImplBuilderImpl<S extends SqlClient>
        extends ReactiveJooqxImplBuilder<S, ReactiveJooqxImpl<S>, ReactiveJooqxImplBuilderImpl<S>> {

        private ReactiveJooqxImplBuilderImpl()           {}

        protected ReactiveJooqxImplBuilderImpl<S> self() {return this;}

        public ReactiveJooqxImpl<S> build()              {return new ReactiveJooqxImpl<S>(this);}

    }

}
