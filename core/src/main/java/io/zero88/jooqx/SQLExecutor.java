package io.zero88.jooqx;

import org.jooq.DSLContext;
import org.jooq.Query;
import org.jooq.TableLike;

import io.vertx.codegen.annotations.Nullable;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.ext.sql.SQLClient;
import io.vertx.sqlclient.SqlClient;
import io.zero88.jooqx.adapter.SQLResultAdapter;
import io.zero88.jooqx.datatype.DataTypeMapperRegistry;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * Represents for an executor that executes {@code jOOQ query} on {@code Vertx SQL client} connection
 *
 * @param <S>  Type of Vertx SQL client. Might be {@link SqlClient} or {@link SQLClient}
 * @param <B>  Type of Vertx SQL bind value holder
 * @param <RS> Type of Vertx SQL result set holder
 * @param <C>  Type of SQL result set collector
 * @see LegacyJooqx
 * @see ReactiveJooqx
 * @see SQLBatchExecutor
 * @since 1.0.0
 */
public interface SQLExecutor<S, B, P extends SQLPreparedQuery<B>, RS, C extends SQLResultCollector<RS>>
    extends SQLBatchExecutor {

    /**
     * Vertx
     *
     * @return vertx
     */
    @NonNull Vertx vertx();

    /**
     * Define jOOQ DSL context
     *
     * @return DSL context
     * @see DSLContext
     */
    @NonNull DSLContext dsl();

    /**
     * Defines sql client
     *
     * @return sql client
     */
    @NonNull S sqlClient();

    /**
     * Defines prepared query
     *
     * @return prepared query
     * @see SQLPreparedQuery
     */
    @NonNull P preparedQuery();

    /**
     * Defines result collector depends on result set
     *
     * @return result collector
     * @see SQLResultCollector
     */
    @NonNull C resultCollector();

    /**
     * Defines an error converter that rethrows an uniform exception by
     * {@link SQLErrorConverter#reThrowError(Throwable)}
     * if any error in execution time
     *
     * @return error handler
     * @apiNote Default is {@link SQLErrorConverter#DEFAULT} that keeps error as it is
     * @see SQLErrorConverter
     */
    @NonNull SQLErrorConverter errorConverter();

    /**
     * Defines global data type mapper registry
     *
     * @return registry
     * @see DataTypeMapperRegistry
     */
    @NonNull DataTypeMapperRegistry typeMapperRegistry();

    /**
     * Execute {@code jOOQ query} then return async result
     *
     * @param <T>           type of jOOQ TableLike
     * @param <R>           type of expectation result
     * @param query         jOOQ query
     * @param resultAdapter a result adapter
     * @param handler       a async result handler
     * @see Query
     * @see TableLike
     * @see SQLResultAdapter
     */
    default <T, R> void execute(@NonNull Query query, @NonNull SQLResultAdapter<T, R> resultAdapter,
                                @NonNull Handler<AsyncResult<@Nullable R>> handler) {
        execute(query, resultAdapter).onComplete(handler);
    }

    /**
     * Like {@link #execute(Query, SQLResultAdapter, Handler)} but returns a {@code Future} of the asynchronous result
     *
     * @param <T>           type of jOOQ TableLike
     * @param <R>           type of expectation result
     * @param query         jOOQ query
     * @param resultAdapter a result adapter
     * @return a {@code Future} of the asynchronous result
     */
    <T, R> Future<@Nullable R> execute(@NonNull Query query, @NonNull SQLResultAdapter<T, R> resultAdapter);

    /**
     * Open transaction executor
     *
     * @param <E> Type of VertxJooqExecutor
     * @return transaction executor
     * @see SQLTxExecutor
     */
    @NonNull <E extends SQLExecutor<S, B, P, RS, C>> SQLTxExecutor<S, B, P, RS, C, E> transaction();

    @Getter
    @Setter
    @Accessors(fluent = true)
    abstract class SQLExecutorBuilder<S, B, P extends SQLPreparedQuery<B>, RS, C extends SQLResultCollector<RS>,
                                             E extends SQLExecutor<S, B, P, RS, C>> {

        @NonNull
        private Vertx vertx;
        @NonNull
        private DSLContext dsl;
        @NonNull
        private S sqlClient;
        private P preparedQuery;
        private C resultCollector;
        private SQLErrorConverter errorConverter;
        private DataTypeMapperRegistry typeMapperRegistry;

        public abstract E build();

    }

}
