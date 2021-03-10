package io.github.zero88.jooq.vertx;

import java.util.function.Function;

import org.jooq.Query;
import org.jooq.TableLike;

import io.github.zero88.jooq.vertx.adapter.SelectListResultAdapter;
import io.github.zero88.jooq.vertx.adapter.SqlResultAdapter;
import io.github.zero88.jooq.vertx.converter.ReactiveBindParamConverter;
import io.github.zero88.jooq.vertx.converter.ReactiveResultBatchConverter;
import io.github.zero88.jooq.vertx.converter.ResultSetConverter;
import io.vertx.core.Future;
import io.vertx.sqlclient.Pool;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import io.vertx.sqlclient.SqlClient;
import io.vertx.sqlclient.SqlConnection;
import io.vertx.sqlclient.Transaction;
import io.vertx.sqlclient.Tuple;

import lombok.Builder.Default;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

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
@SuperBuilder
@Accessors(fluent = true)
public final class VertxReactiveSqlExecutor<S extends SqlClient>
    extends AbstractVertxJooqExecutor<S, Tuple, RowSet<Row>>
    implements VertxTxExecutor<S, Tuple, RowSet<Row>, VertxReactiveSqlExecutor<S>>,
               VertxReactiveBatchExecutor {

    @NonNull
    @Default
    private final QueryHelper<Tuple> helper = new QueryHelper<>(new ReactiveBindParamConverter());

    @Override
    public <Q extends Query, T extends TableLike<?>, C extends ResultSetConverter<RowSet<Row>>, R> Future<R> execute(
        @NonNull Q query, @NonNull SqlResultAdapter<RowSet<Row>, C, T, R> resultAdapter) {
        return sqlClient().preparedQuery(helper().toPreparedQuery(dsl().configuration(), query))
                          .execute(helper().toBindValues(query))
                          .map(resultAdapter::convert)
                          .otherwise(errorConverter()::reThrowError);
    }

    @Override
    @SuppressWarnings("unchecked")
    public @NonNull VertxTxExecutor<S, Tuple, RowSet<Row>, VertxReactiveSqlExecutor<S>> transaction() {
        return this;
    }

    @Override
    public <Q extends Query> Future<BatchResult> batch(@NonNull Q query, @NonNull BindBatchValues bindBatchValues) {
        return sqlClient().preparedQuery(helper().toPreparedQuery(dsl().configuration(), query))
                          .executeBatch(helper().toBindValues(query, bindBatchValues))
                          .map(r -> new ReactiveResultBatchConverter().batchResultSize(r))
                          .map(s -> new BatchResult(bindBatchValues.size(), s))
                          .otherwise(errorConverter()::reThrowError);
    }

    @Override
    public <Q extends Query, T extends TableLike<?>, R> Future<BatchReturningResult<R>> batch(@NonNull Q query,
                                                                                              @NonNull BindBatchValues bindBatchValues,
                                                                                              @NonNull SelectListResultAdapter<RowSet<Row>, ReactiveResultBatchConverter, T, R> adapter) {
        return sqlClient().preparedQuery(helper().toPreparedQuery(dsl().configuration(), query))
                          .executeBatch(helper().toBindValues(query, bindBatchValues))
                          .map(adapter::convert)
                          .map(rs -> new BatchReturningResult<>(bindBatchValues.size(), rs))
                          .otherwise(errorConverter()::reThrowError);
    }

    @Override
    public <X> Future<X> run(@NonNull Function<VertxReactiveSqlExecutor<S>, Future<X>> function) {
        final S c = sqlClient();
        if (c instanceof Pool) {
            return ((Pool) c).getConnection()
                             .compose(conn -> beginTx(conn, function),
                                      t -> Future.failedFuture(connFailed("Unable to open SQL connection", t)));
        }
        String msg = c instanceof SqlConnection
                     ? "Unsupported using connection on SQL connection: [" + c.getClass() + "]. Switch using SQL pool"
                     : "Unable to open transaction due to unknown SQL client: [" + c.getClass() + "]";
        // TODO: maybe with SQL client provider in VertxReactiveBuilder to spawn new conn
        //        if (c instanceof SqlConnection) {
        //            return beginTransaction((SqlConnection) client, transaction);
        //        }
        return Future.failedFuture(connFailed(msg));
    }

    @Override
    protected VertxReactiveSqlExecutor<S> withSqlClient(@NonNull S sqlClient) {
        return VertxReactiveSqlExecutor.<S>builder().vertx(vertx())
                                                    .sqlClient(sqlClient)
                                                    .dsl(dsl())
                                                    .helper(helper())
                                                    .errorConverter(errorConverter())
                                                    .build();
    }

    @SuppressWarnings("unchecked")
    private <X> Future<X> beginTx(@NonNull SqlConnection conn,
                                  @NonNull Function<VertxReactiveSqlExecutor<S>, Future<X>> transaction) {
        return conn.begin()
                   .flatMap(tx -> transaction.apply(withSqlClient((S) conn))
                                             .compose(res -> tx.commit().flatMap(v -> Future.succeededFuture(res)),
                                                      err -> rollbackHandler(tx, errorConverter().handle(err))))
                   .onComplete(ar -> conn.close());
    }

    private <X> Future<X> rollbackHandler(@NonNull Transaction tx, @NonNull RuntimeException t) {
        return tx.rollback().compose(v -> Future.failedFuture(t), failure -> Future.failedFuture(t));
    }

}
