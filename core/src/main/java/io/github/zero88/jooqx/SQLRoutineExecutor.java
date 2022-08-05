package io.github.zero88.jooqx;

import java.util.function.Function;

import org.jetbrains.annotations.ApiStatus.Experimental;
import org.jetbrains.annotations.NotNull;
import org.jooq.DSLContext;
import org.jooq.Routine;
import org.jooq.Support;

import io.github.zero88.jooqx.adapter.SQLResultAdapter;
import io.github.zero88.jooqx.annotations.SQLClientSupport;
import io.github.zero88.jooqx.annotations.SQLClientType;
import io.github.zero88.jooqx.routine.RoutineResult;
import io.vertx.codegen.annotations.GenIgnore;
import io.vertx.codegen.annotations.Nullable;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;

/**
 * Represents for a {@code routine executor} that executes SQL function or SQL procedure
 *
 * @since 2.0.0
 */
@Experimental
@VertxGen(concrete = false)
public interface SQLRoutineExecutor extends JooqDSLProvider {

    @Experimental
    @GenIgnore(GenIgnore.PERMITTED_TYPE)
    @SQLClientSupport(client = {
        SQLClientType.JDBC, SQLClientType.MYSQL, SQLClientType.POSTGRES
    }, dialect = @Support())
    default <T> void routine(@NotNull Function<DSLContext, Routine<T>> routineFunction,
                             @NotNull Handler<AsyncResult<T>> handler) {
        routine(routineFunction).onComplete(handler);
    }

    @Experimental
    @GenIgnore(GenIgnore.PERMITTED_TYPE)
    @SQLClientSupport(dialect = @Support(), client = {
        SQLClientType.JDBC, SQLClientType.MYSQL, SQLClientType.POSTGRES
    })
    default <T> Future<@Nullable T> routine(@NotNull Function<DSLContext, Routine<T>> routineFunction) {
        return routine(routineFunction.apply(dsl()));
    }

    @Experimental
    @GenIgnore(GenIgnore.PERMITTED_TYPE)
    @SQLClientSupport(dialect = @Support(), client = {
        SQLClientType.JDBC, SQLClientType.MYSQL, SQLClientType.POSTGRES
    })
    default <T> Future<@Nullable T> routine(@NotNull Routine<T> routine, @NotNull Handler<AsyncResult<T>> handler) {
        return routine(routine).onComplete(handler);
    }

    @Experimental
    @GenIgnore(GenIgnore.PERMITTED_TYPE)
    @SQLClientSupport(client = {
        SQLClientType.JDBC, SQLClientType.MYSQL, SQLClientType.POSTGRES
    }, dialect = @Support())
    <T> Future<@Nullable T> routine(@NotNull Routine<T> routine);

    @Experimental
    @GenIgnore(GenIgnore.PERMITTED_TYPE)
    @SQLClientSupport(client = {
        SQLClientType.JDBC, SQLClientType.MYSQL, SQLClientType.POSTGRES
    }, dialect = @Support())
    default <T> void routineResult(@NotNull Routine<T> routine, @NotNull Handler<AsyncResult<RoutineResult>> handler) {
        routineResult(routine).onComplete(handler);
    }

    @Experimental
    @GenIgnore(GenIgnore.PERMITTED_TYPE)
    @SQLClientSupport(client = {
        SQLClientType.JDBC, SQLClientType.MYSQL, SQLClientType.POSTGRES
    }, dialect = @Support())
    <T> Future<RoutineResult> routineResult(@NotNull Routine<T> routine);

    @Experimental
    @GenIgnore(GenIgnore.PERMITTED_TYPE)
    @SQLClientSupport(client = { SQLClientType.JDBC, SQLClientType.MYSQL }, dialect = @Support())
    default <T, X, R> Future<@Nullable R> routineResultSet(@NotNull Routine<T> routine,
                                                           @NotNull SQLResultAdapter<X, R> resultAdapter,
                                                           @NotNull Handler<AsyncResult<R>> handler) {
        return routineResultSet(routine, resultAdapter).onComplete(handler);
    }

    @Experimental
    @GenIgnore(GenIgnore.PERMITTED_TYPE)
    @SQLClientSupport(client = { SQLClientType.JDBC, SQLClientType.MYSQL }, dialect = @Support())
    <T, X, R> Future<@Nullable R> routineResultSet(@NotNull Routine<T> routine,
                                                   @NotNull SQLResultAdapter<X, R> resultAdapter);

}
