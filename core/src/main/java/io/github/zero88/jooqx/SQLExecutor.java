package io.github.zero88.jooqx;

import org.jetbrains.annotations.NotNull;
import org.jooq.Routine;

import io.github.zero88.jooqx.adapter.SQLResultAdapter;
import io.github.zero88.jooqx.datatype.DataTypeMapperRegistry;
import io.github.zero88.jooqx.routine.RoutineExecutorDelegate;
import io.github.zero88.jooqx.routine.RoutineResult;
import io.vertx.core.Future;
import io.vertx.core.Vertx;

/**
 * Represents for an executor that executes {@code jOOQ query} on {@code Vertx SQL client} connection
 *
 * @param <S>  Type of Vertx SQL client
 * @param <B>  Type of Vertx SQL bind value holder
 * @param <PQ> Type of SQL prepare query
 * @param <RC> Type of SQL result set collector
 * @see JooqDSLProvider
 * @see SQLBatchExecutor
 * @see SQLDDLExecutor
 * @see SQLPlainExecutor
 * @see SQLQueryExecutor
 * @see SQLRoutineExecutor
 * @since 1.0.0
 */
public interface SQLExecutor<S, B, PQ extends SQLPreparedQuery<B>, RC extends SQLResultCollector>
    extends SQLExecutorContext<S, B, PQ, RC>, SQLQueryExecutor, SQLBatchExecutor, SQLDDLExecutor, SQLRoutineExecutor,
            SQLPlainExecutor {

    /**
     * Defines Vertx
     *
     * @return vertx
     */
    @NotNull Vertx vertx();

    /**
     * Defines Vertx SQL client
     *
     * @return sql client
     */
    @NotNull S sqlClient();

    /**
     * Defines prepared query
     *
     * @return prepared query
     * @see SQLPreparedQuery
     */
    PQ preparedQuery();

    /**
     * Defines result collector depends on result set
     *
     * @return result collector
     * @see SQLResultCollector
     */
    RC resultCollector();

    /**
     * Defines an error converter that rethrows a uniform exception by {@link SQLErrorConverter#reThrowError(Throwable)}
     * if any error in execution time
     *
     * @return error handler
     * @apiNote Default is {@link SQLErrorConverter#DEFAULT} that keeps error as it is
     * @see SQLErrorConverter
     */
    SQLErrorConverter errorConverter();

    /**
     * Defines global data type mapper registry
     *
     * @return registry
     * @see DataTypeMapperRegistry
     */
    DataTypeMapperRegistry typeMapperRegistry();

    /**
     * Open transaction executor
     *
     * @param <E> Type of VertxJooqExecutor
     * @return transaction executor
     * @see SQLTxExecutor
     */
    @NotNull <E extends SQLExecutor<S, B, PQ, RC>> SQLTxExecutor<S, B, PQ, RC, E> transaction();

    @Override
    default <T> Future<T> routine(@NotNull Routine<T> routine) {
        return RoutineExecutorDelegate.init(this).routine(routine);
    }

    @Override
    default <T> Future<RoutineResult> routineResult(@NotNull Routine<T> routine) {
        return RoutineExecutorDelegate.init(this).routineResult(routine);
    }

    @Override
    default <T, X, R> Future<R> routineResultSet(@NotNull Routine<T> routine,
                                                 @NotNull SQLResultAdapter<X, R> resultAdapter) {
        return RoutineExecutorDelegate.init(this).routineResultSet(routine, resultAdapter);
    }

}
