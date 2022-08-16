package io.github.zero88.jooqx;

import java.util.function.Function;

import org.jetbrains.annotations.ApiStatus.Experimental;
import org.jetbrains.annotations.NotNull;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.Support;

import io.github.zero88.jooqx.annotations.SQLClientSupport;
import io.github.zero88.jooqx.annotations.SQLClientType;
import io.vertx.codegen.annotations.GenIgnore;
import io.vertx.codegen.annotations.Unstable;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;

/**
 * Represents for a {@code Block executor} that executes SQL statements
 *
 * @since 2.0.0
 */
@Unstable
@Experimental
@VertxGen(concrete = false)

public interface SQLBlockExecutor extends JooqDSLProvider {

    /**
     * Block execute
     *
     * @param blockQueryFn the block of SQL queries function
     * @param handler      the async result handler
     * @see BlockQuery
     * @see BlockResult
     * @since 2.0.0
     */
    @Unstable
    @Experimental
    @GenIgnore(GenIgnore.PERMITTED_TYPE)
    @SQLClientSupport(client = { SQLClientType.JDBC }, dialect = @Support(SQLDialect.POSTGRES))
    default void block(@NotNull Function<DSLContext, BlockQuery> blockQueryFn,
                       @NotNull Handler<AsyncResult<BlockResult>> handler) {
        block(blockQueryFn).onComplete(handler);
    }

    /**
     * Like {@link #block(Function, Handler)} but returns a {@code Future} of the asynchronous result
     *
     * @param blockQueryFn block queries function
     * @return a {@code Future} of the asynchronous block result
     * @see BlockResult
     * @since 2.0.0
     */
    @Unstable
    @Experimental
    @GenIgnore(GenIgnore.PERMITTED_TYPE)
    @SQLClientSupport(client = { SQLClientType.JDBC }, dialect = @Support(SQLDialect.POSTGRES))
    default Future<BlockResult> block(@NotNull Function<DSLContext, BlockQuery> blockQueryFn) {
        return block(blockQueryFn.apply(dsl()));
    }

    /**
     * Block execute
     *
     * @param blockQuery the block of SQL queries
     * @param handler    the async result handler
     * @see BlockQuery
     * @see BlockResult
     * @since 2.0.0
     */
    @Unstable
    @Experimental
    @GenIgnore(GenIgnore.PERMITTED_TYPE)
    @SQLClientSupport(client = { SQLClientType.JDBC }, dialect = @Support(SQLDialect.POSTGRES))
    default void block(@NotNull BlockQuery blockQuery, @NotNull Handler<AsyncResult<BlockResult>> handler) {
        block(blockQuery).onComplete(handler);
    }

    /**
     * Like {@link #block(BlockQuery, Handler)} but returns a {@code Future} of the asynchronous result
     *
     * @param blockQuery the block of SQL queries
     * @return a {@code Future} of the asynchronous block result
     * @see BlockQuery
     * @see BlockResult
     * @since 2.0.0
     */
    @Unstable
    @Experimental
    @GenIgnore(GenIgnore.PERMITTED_TYPE)
    @SQLClientSupport(client = { SQLClientType.JDBC }, dialect = @Support(SQLDialect.POSTGRES))
    Future<BlockResult> block(@NotNull BlockQuery blockQuery);

}
