package io.zero88.jooqx;

import java.util.function.Function;

import org.jetbrains.annotations.NotNull;
import org.jooq.DDLQuery;
import org.jooq.DSLContext;

import io.vertx.codegen.annotations.GenIgnore;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;

/**
 * Represents for a {@code DDL executor} that executes batch SQL command
 *
 * @since 1.1.0
 */
@VertxGen(concrete = false)
public interface SQLDDLExecutor extends JooqDSLProvider {

    /**
     * Execute DDL statement
     *
     * @param ddlFunction DDL function produces DDL statement
     * @param handler     async result handler
     * @since 1.1.0
     */
    @GenIgnore(GenIgnore.PERMITTED_TYPE)
    default void ddl(@NotNull Function<DSLContext, DDLQuery> ddlFunction,
                     @NotNull Handler<AsyncResult<Integer>> handler) {
        ddl(ddlFunction).onComplete(handler);
    }

    /**
     * Like {@link #ddl(Function, Handler)} but returns a {@code Future} of the asynchronous result
     *
     * @param ddlFunction DDL function produces DDL statement
     * @return a {@code Future} of the asynchronous result
     * @since 1.1.0
     */
    @GenIgnore(GenIgnore.PERMITTED_TYPE)
    default Future<Integer> ddl(@NotNull Function<DSLContext, DDLQuery> ddlFunction) {
        return ddl(ddlFunction.apply(dsl()));
    }

    /**
     * Execute DDL statement
     *
     * @param statement DDL statement
     * @param handler   async result handler
     * @since 1.1.0
     */
    @GenIgnore(GenIgnore.PERMITTED_TYPE)
    default void ddl(@NotNull DDLQuery statement, @NotNull Handler<AsyncResult<Integer>> handler) {
        ddl(statement).onComplete(handler);
    }

    /**
     * Like {@link #ddl(DDLQuery, Handler)} but returns a {@code Future} of the asynchronous result
     *
     * @param statement DDL statement
     * @return a {@code Future} of the asynchronous result
     * @since 1.1.0
     */
    @GenIgnore(GenIgnore.PERMITTED_TYPE)
    Future<Integer> ddl(@NotNull DDLQuery statement);

}
