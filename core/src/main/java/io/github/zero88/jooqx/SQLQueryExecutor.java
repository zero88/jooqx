package io.github.zero88.jooqx;

import java.util.List;
import java.util.function.Function;

import org.jetbrains.annotations.NotNull;
import org.jooq.DSLContext;
import org.jooq.Query;
import org.jooq.Record;
import org.jooq.Select;
import org.jooq.Table;
import org.jooq.TableLike;
import org.jooq.impl.DSL;

import io.github.zero88.jooqx.adapter.SQLResultAdapter;
import io.vertx.codegen.annotations.GenIgnore;
import io.vertx.codegen.annotations.Nullable;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;

/**
 * Represents for a {@code query executor} that executes SQL command
 *
 * @since 2.0.0
 */
@VertxGen(concrete = false)
public interface SQLQueryExecutor extends JooqDSLProvider {

    /**
     * Execute {@code jOOQ query} then return async result
     *
     * @param queryFunction the jOOQ query function
     * @param resultAdapter the result adapter
     * @param handler       the async result handler
     * @see Query
     * @see TableLike
     * @see SQLResultAdapter
     * @since 2.0.0
     */
    @GenIgnore(GenIgnore.PERMITTED_TYPE)
    default <T, R> void execute(@NotNull Function<DSLContext, Query> queryFunction,
                                @NotNull SQLResultAdapter<T, R> resultAdapter,
                                @NotNull Handler<AsyncResult<@Nullable R>> handler) {
        execute(queryFunction, resultAdapter).onComplete(handler);
    }

    /**
     * Like {@link #execute(Function, SQLResultAdapter, Handler)} but returns a {@code Future} of the asynchronous
     * result
     *
     * @param queryFunction the jOOQ query function
     * @param resultAdapter the result adapter
     * @return a {@code Future} of the asynchronous result
     * @since 2.0.0
     */
    @GenIgnore(GenIgnore.PERMITTED_TYPE)
    default <T, R> Future<@Nullable R> execute(@NotNull Function<DSLContext, Query> queryFunction,
                                               @NotNull SQLResultAdapter<T, R> resultAdapter) {
        return execute(queryFunction.apply(dsl()), resultAdapter);
    }

    /**
     * Execute {@code jOOQ query} then return async result
     *
     * @param query         the jOOQ query
     * @param resultAdapter the result adapter
     * @param handler       the async result handler
     * @see Query
     * @see TableLike
     * @see SQLResultAdapter
     */
    @GenIgnore(GenIgnore.PERMITTED_TYPE)
    default <T, R> void execute(@NotNull Query query, @NotNull SQLResultAdapter<T, R> resultAdapter,
                                @NotNull Handler<AsyncResult<@Nullable R>> handler) {
        execute(query, resultAdapter).onComplete(handler);
    }

    /**
     * Like {@link #execute(Query, SQLResultAdapter, Handler)} but returns a {@code Future} of the asynchronous result
     *
     * @param query         the jOOQ query
     * @param resultAdapter the result adapter
     * @return a {@code Future} of the asynchronous result
     */
    @GenIgnore(GenIgnore.PERMITTED_TYPE)
    <T, R> Future<@Nullable R> execute(@NotNull Query query, @NotNull SQLResultAdapter<T, R> resultAdapter);

    /**
     * Shortcut of {@link #execute(Function, SQLResultAdapter, Handler)} with SQLResultAdapter is
     * {@link DSLAdapter#fetchOne(Table)}
     *
     * @param selectFn the jOOQ select function
     * @param handler  the async result handler
     * @param <REC>    Type of Record
     * @apiNote Unfortunately, it is not support {@code rxify/mutiny} version due to technical problem in
     *     {@code Vert.x} code generator
     * @since 2.0.0
     */
    @GenIgnore
    default <REC extends Record> void fetchOne(@NotNull Function<DSLContext, Select<REC>> selectFn,
                                               @NotNull Handler<AsyncResult<@Nullable REC>> handler) {
        fetchOne(selectFn).onComplete(handler);
    }

    /**
     * Like {@link #fetchOne(Function, Handler)} but returns a {@code Future} of the asynchronous result
     *
     * @param selectFn the jOOQ select function
     * @return a {@code Future} of the asynchronous result
     * @apiNote Unfortunately, it is not support {@code rxify/mutiny} version due to technical problem in
     *     {@code Vert.x} code generator
     * @since 2.0.0
     */
    @GenIgnore
    default <REC extends Record> Future<@Nullable REC> fetchOne(@NotNull Function<DSLContext, Select<REC>> selectFn) {
        return fetchOne(selectFn.apply(dsl()));
    }

    /**
     * Shortcut of {@link #execute(Query, SQLResultAdapter)} with SQLResultAdapter is
     * {@link DSLAdapter#fetchOne(Table)}
     *
     * @param select  the jOOQ select
     * @param handler the async result handler
     * @param <REC>   Type of Record
     * @apiNote Unfortunately, it is not support {@code rxify/mutiny} version due to technical problem in
     *     {@code Vert.x} code generator
     * @see Select
     * @since 2.0.0
     */
    @GenIgnore
    default <REC extends Record> void fetchOne(@NotNull Select<REC> select,
                                               @NotNull Handler<AsyncResult<@Nullable REC>> handler) {
        fetchOne(select).onComplete(handler);
    }

    /**
     * Like {@link #fetchOne(Select, Handler)} but returns a {@code Future} of the asynchronous result
     *
     * @param select the jOOQ select function
     * @return a {@code Future} of the asynchronous result
     * @apiNote Unfortunately, it is not support {@code rxify/mutiny} version due to technical problem in
     *     {@code Vert.x} code generator
     * @see Select
     * @since 2.0.0
     */
    @GenIgnore
    default <REC extends Record> Future<@Nullable REC> fetchOne(@NotNull Select<REC> select) {
        return execute(select, DSLAdapter.fetchOne(select.asTable()));
    }

    /**
     * Shortcut of {@link #execute(Function, SQLResultAdapter, Handler)} with SQLResultAdapter is
     * {@link DSLAdapter#fetchMany(Table)}
     *
     * @param selectFn the jOOQ select function
     * @param handler  the async result handler
     * @param <REC>    Type of Record
     * @apiNote Unfortunately, it is not support {@code rxify/mutiny} version due to technical problem in
     *     {@code Vert.x} code generator
     * @see Select
     * @since 2.0.0
     */
    @GenIgnore
    default <REC extends Record> void fetchMany(@NotNull Function<DSLContext, Select<REC>> selectFn,
                                                @NotNull Handler<AsyncResult<List<REC>>> handler) {
        fetchMany(selectFn).onComplete(handler);
    }

    /**
     * Like {@link #fetchMany(Function, Handler)}  but returns a {@code Future} of the asynchronous result
     *
     * @param selectFn the jOOQ select function
     * @return a {@code Future} of the asynchronous result
     * @apiNote Unfortunately, it is not support {@code rxify/mutiny} version due to technical problem in
     *     {@code Vert.x} code generator
     * @see Select
     * @since 2.0.0
     */
    @GenIgnore
    default <REC extends Record> Future<List<REC>> fetchMany(@NotNull Function<DSLContext, Select<REC>> selectFn) {
        return fetchMany(selectFn.apply(dsl()));
    }

    /**
     * Shortcut of {@link #execute(Query, SQLResultAdapter, Handler)} with SQLResultAdapter is
     * {@link DSLAdapter#fetchMany(Table)}
     *
     * @param select  the jOOQ select
     * @param handler the async result handler
     * @param <REC>   Type of Record
     * @apiNote Unfortunately, it is not support {@code rxify/mutiny} version due to technical problem in
     *     {@code Vert.x} code generator
     * @see Select
     * @since 2.0.0
     */
    @GenIgnore
    default <REC extends Record> void fetchMany(@NotNull Select<REC> select,
                                                @NotNull Handler<AsyncResult<List<REC>>> handler) {
        fetchMany(select).onComplete(handler);
    }

    /**
     * Like {@link #fetchMany(Select, Handler)}  but returns a {@code Future} of the asynchronous result
     *
     * @param select the jOOQ select function
     * @return a {@code Future} of the asynchronous result
     * @apiNote Unfortunately, it is not support {@code rxify/mutiny} version due to technical problem in
     *     {@code Vert.x} code generator
     * @see Select
     * @since 2.0.0
     */
    @GenIgnore
    default <REC extends Record> Future<List<REC>> fetchMany(@NotNull Select<REC> select) {
        return execute(select, DSLAdapter.fetchMany(select.asTable()));
    }

    /**
     * Shortcut of {@link #execute(Function, SQLResultAdapter, Handler)} with SQLResultAdapter is
     * {@link DSLAdapter#fetchExists()}
     *
     * @param selectFn the jOOQ select function
     * @param handler  the async result handler
     * @param <REC>    Type of Record
     * @apiNote Unfortunately, it is not support {@code rxify/mutiny} version due to technical problem in
     *     {@code Vert.x} code generator
     * @see Select
     * @since 2.0.0
     */
    @GenIgnore
    default <REC extends Record> void fetchExists(@NotNull Function<DSLContext, Select<REC>> selectFn,
                                                  @NotNull Handler<AsyncResult<Boolean>> handler) {
        fetchExists(selectFn.apply(dsl())).onComplete(handler);
    }

    /**
     * Like {@link #fetchExists(Function, Handler)}  but returns a {@code Future} of the asynchronous result
     *
     * @param selectFn the jOOQ select function
     * @param <REC>    Type of Record
     * @apiNote Unfortunately, it is not support {@code rxify/mutiny} version due to technical problem in
     *     {@code Vert.x} code generator
     * @see Select
     * @since 2.0.0
     */
    @GenIgnore
    default <REC extends Record> Future<Boolean> fetchExists(@NotNull Function<DSLContext, Select<REC>> selectFn) {
        return fetchExists(selectFn.apply(dsl()));
    }

    /**
     * Shortcut of {@link #execute(Query, SQLResultAdapter, Handler)} with SQLResultAdapter is
     * {@link DSLAdapter#fetchExists()}
     *
     * @param select  the jOOQ select
     * @param handler the async result handler
     * @param <REC>   Type of Record
     * @apiNote Unfortunately, it is not support {@code rxify/mutiny} version due to technical problem in
     *     {@code Vert.x} code generator
     * @see Select
     * @since 2.0.0
     */
    @GenIgnore
    default <REC extends Record> void fetchExists(@NotNull Select<REC> select,
                                                  @NotNull Handler<AsyncResult<Boolean>> handler) {
        fetchExists(select).onComplete(handler);
    }

    /**
     * Like {@link #fetchExists(Select, Handler)}  but returns a {@code Future} of the asynchronous result
     *
     * @param select the jOOQ select
     * @param <REC>  Type of Record
     * @apiNote Unfortunately, it is not support {@code rxify/mutiny} version due to technical problem in
     *     {@code Vert.x} code generator
     * @see Select
     * @since 2.0.0
     */
    @GenIgnore
    default <REC extends Record> Future<Boolean> fetchExists(@NotNull Select<REC> select) {
        return execute(DSL.selectOne().where(DSL.exists(select)), DSLAdapter.fetchExists());
    }

    /**
     * Shortcut of {@link #execute(Function, SQLResultAdapter, Handler)} with SQLResultAdapter is
     * {@link DSLAdapter#fetchCount()}
     *
     * @param selectFn the jOOQ select function
     * @param <REC>    Type of Record
     * @apiNote Unfortunately, it is not support {@code rxify/mutiny} version due to technical problem in
     *     {@code Vert.x} code generator
     * @see Select
     * @since 2.0.0
     */
    @GenIgnore
    default <REC extends Record> void fetchCount(@NotNull Function<DSLContext, Select<REC>> selectFn,
                                                 @NotNull Handler<AsyncResult<Integer>> handler) {
        fetchCount(selectFn).onComplete(handler);
    }

    /**
     * Like {@link #fetchCount(Function, Handler)} but returns a {@code Future} of the asynchronous result
     *
     * @param selectFn the jOOQ select function
     * @param <REC>    Type of Record
     * @apiNote Unfortunately, it is not support {@code rxify/mutiny} version due to technical problem in
     *     {@code Vert.x} code generator
     * @see Select
     * @since 2.0.0
     */
    @GenIgnore
    default <REC extends Record> Future<Integer> fetchCount(@NotNull Function<DSLContext, Select<REC>> selectFn) {
        return fetchCount(selectFn.apply(dsl()));
    }

    /**
     * Shortcut of {@link #execute(Query, SQLResultAdapter, Handler)} with SQLResultAdapter is
     * {@link DSLAdapter#fetchCount()}
     *
     * @param select  the jOOQ select
     * @param handler the async result handler
     * @param <REC>   Type of Record
     * @apiNote Unfortunately, it is not support {@code rxify/mutiny} version due to technical problem in
     *     {@code Vert.x} code generator
     * @see Select
     * @since 2.0.0
     */
    @GenIgnore
    default <REC extends Record> void fetchCount(@NotNull Select<REC> select,
                                                 @NotNull Handler<AsyncResult<Integer>> handler) {
        fetchCount(select).onComplete(handler);
    }

    /**
     * Like {@link #fetchCount(Select, Handler)} but returns a {@code Future} of the asynchronous result
     *
     * @param select the jOOQ select
     * @param <REC>  Type of Record
     * @apiNote Unfortunately, it is not support {@code rxify/mutiny} version due to technical problem in
     *     {@code Vert.x} code generator
     * @see Select
     * @since 2.0.0
     */
    @GenIgnore
    default <REC extends Record> Future<Integer> fetchCount(@NotNull Select<REC> select) {
        return execute(DSL.selectCount().from(select), DSLAdapter.fetchCount());
    }

}
