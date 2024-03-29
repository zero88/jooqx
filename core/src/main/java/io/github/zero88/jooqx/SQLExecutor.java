package io.github.zero88.jooqx;

import org.jetbrains.annotations.ApiStatus.Experimental;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;
import org.jooq.Routine;
import org.jooq.Support;

import io.github.zero88.jooqx.adapter.SQLResultAdapter;
import io.github.zero88.jooqx.annotations.SQLClientSupport;
import io.github.zero88.jooqx.annotations.SQLClientType;
import io.github.zero88.jooqx.datatype.DataTypeMapperRegistry;
import io.github.zero88.jooqx.routine.RoutineExecutorDelegate;
import io.github.zero88.jooqx.routine.RoutineResult;
import io.vertx.codegen.annotations.GenIgnore;
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
 * @see SQLStatementExecutor
 * @see SQLBatchExecutor
 * @see SQLBlockExecutor
 * @see SQLRoutineExecutor
 * @see SQLDQLExecutor
 * @see SQLDMLExecutor
 * @see SQLDDLExecutor
 * @see SQLPlainExecutor
 * @since 1.0.0
 */
public interface SQLExecutor<S, B, PQ extends SQLPreparedQuery<B>, RC extends SQLResultCollector>
    extends SQLExecutorContext<S, B, PQ, RC>, SQLStatementExecutor, SQLBatchExecutor, SQLBlockExecutor,
            SQLRoutineExecutor, SQLDQLExecutor, SQLDMLExecutor, SQLDDLExecutor, SQLPlainExecutor {

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

    /**
     * Open session executor
     *
     * @param <E> Type of VertxJooqExecutor
     * @return transaction executor
     * @see SQLSessionExecutor
     */
    @NotNull <E extends SQLExecutor<S, B, PQ, RC>> SQLSessionExecutor<S, B, PQ, RC, E> session();

    @Override
    @Experimental
    @GenIgnore(GenIgnore.PERMITTED_TYPE)
    @SQLClientSupport(dialect = @Support(), client = {
        SQLClientType.JDBC, SQLClientType.MYSQL, SQLClientType.POSTGRES
    })
    default <T> Future<T> routine(@NotNull Routine<T> routine) {
        return RoutineExecutorDelegate.init(this).routine(routine);
    }

    @Override
    @Experimental
    @GenIgnore(GenIgnore.PERMITTED_TYPE)
    @SQLClientSupport(dialect = @Support(), client = {
        SQLClientType.JDBC, SQLClientType.MYSQL, SQLClientType.POSTGRES
    })
    default <T> Future<RoutineResult> routineResult(@NotNull Routine<T> routine) {
        return RoutineExecutorDelegate.init(this).routineResult(routine);
    }

    @Override
    @Experimental
    @GenIgnore(GenIgnore.PERMITTED_TYPE)
    @SQLClientSupport(dialect = @Support(), client = { SQLClientType.JDBC, SQLClientType.MYSQL })
    default <T, X, R> Future<R> routineResultSet(@NotNull Routine<T> routine,
                                                 @NotNull SQLResultAdapter<X, R> resultAdapter) {
        return RoutineExecutorDelegate.init(this).routineResultSet(routine, resultAdapter);
    }

    @Override
    @Internal
    @GenIgnore
    default SQLStatementExecutor executor() { return this; }

}
