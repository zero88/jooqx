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
 * @see Tuple
 * @see Row
 * @see RowSet
 * @since 1.0.0
 */
@Getter
@SuperBuilder
@Accessors(fluent = true)
public final class VertxReactiveSqlExecutor<S extends SqlClient>
    extends AbstractVertxJooqExecutor<S, Tuple, RowSet<Row>> implements VertxReactiveBatchExecutor {

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
    public <X> Future<X> withTransaction(
        @NonNull Function<VertxJooqExecutor<S, Tuple, RowSet<Row>>, Future<X>> transaction) {
        final S client = sqlClient();
        if (client instanceof Pool) {
            return ((Pool) client).withTransaction(conn -> transaction.apply(withSqlClient((S) conn)));
        }
        if (client instanceof SqlConnection) {
            final SqlConnection conn = (SqlConnection) client;
            return conn.begin()
                       .flatMap(tx -> transaction.apply(withSqlClient((S) conn))
                                                 .compose(res -> tx.commit().flatMap(v -> Future.succeededFuture(res)),
                                                          err -> tx.rollback()
                                                                   .compose(v -> Future.failedFuture(err),
                                                                            failure -> Future.failedFuture(err))))
                       .onComplete(ar -> conn.close());
        }
        return Future.failedFuture("Unable to open transaction due to unknown sql client type " + client.getClass());
    }

    @Override
    public <Q extends Query> Future<BatchResult> batchExecute(@NonNull Q query,
                                                              @NonNull BindBatchValues bindBatchValues) {
        return sqlClient().preparedQuery(helper().toPreparedQuery(dsl().configuration(), query))
                          .executeBatch(helper().toBindValues(query, bindBatchValues))
                          .map(r -> new ReactiveResultBatchConverter().batchResultSize(r))
                          .map(s -> new BatchResult(bindBatchValues.size(), s))
                          .otherwise(errorConverter()::reThrowError);
    }

    @Override
    public <Q extends Query, T extends TableLike<?>, R> Future<BatchReturningResult<R>> batchExecute(@NonNull Q query,
                                                                                                     @NonNull BindBatchValues bindBatchValues,
                                                                                                     @NonNull SelectListResultAdapter<RowSet<Row>, ReactiveResultBatchConverter, T, R> resultAdapter) {
        return sqlClient().preparedQuery(helper().toPreparedQuery(dsl().configuration(), query))
                          .executeBatch(helper().toBindValues(query, bindBatchValues))
                          .map(resultAdapter::convert)
                          .map(rs -> new BatchReturningResult<>(bindBatchValues.size(), rs))
                          .otherwise(errorConverter()::reThrowError);
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

}
