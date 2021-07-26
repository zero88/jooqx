package io.zero88.jooqx;

import java.util.function.Function;

import org.jooq.DDLQuery;
import org.jooq.DSLContext;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;

import lombok.NonNull;

/**
 * Represents for a {@code DDL executor} that executes batch SQL command
 *
 * @since 1.1.0
 */
public interface SQLDDLExecutor extends JooqDSLProvider {

    /**
     * DDL execute
     *
     * @param ddlFunction DDL function
     * @param handler     async result handler
     * @since 1.1.0
     */
    default void ddl(@NonNull Function<DSLContext, DDLQuery> ddlFunction,
                     @NonNull Handler<AsyncResult<Integer>> handler) {
        ddl(ddlFunction).onComplete(handler);
    }

    /**
     * Like {@link #ddl(Function, Handler)} but returns a {@code Future} of the asynchronous result
     *
     * @param ddlFunction DDL function
     * @return a {@code Future} of the asynchronous result
     * @since 1.1.0
     */
    default Future<Integer> ddl(@NonNull Function<DSLContext, DDLQuery> ddlFunction) {
        return ddl(ddlFunction.apply(dsl()));
    }

    /**
     * DDL execute
     *
     * @param query   DDL query
     * @param handler async result handler
     * @since 1.1.0
     */
    default void ddl(@NonNull DDLQuery query, @NonNull Handler<AsyncResult<Integer>> handler) {
        ddl(query).onComplete(handler);
    }

    /**
     * Like {@link #ddl(DDLQuery, Handler)} but returns a {@code Future} of the asynchronous result
     *
     * @param query DDL query
     * @return a {@code Future} of the asynchronous result
     * @since 1.1.0
     */
    Future<Integer> ddl(@NonNull DDLQuery query);

}
