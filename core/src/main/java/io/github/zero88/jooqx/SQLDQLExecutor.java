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
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.spi.json.JsonCodec;

/**
 * Represents for a {@code query executor} that executes SQL command
 *
 * @since 2.0.0
 */
@VertxGen(concrete = false)
public interface SQLDQLExecutor extends JooqDSLProvider, HasExecutor {

    /**
     * Shortcut of {@link SQLStatementExecutor#execute(Function, SQLResultAdapter, Handler)} with SQLResultAdapter is
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
     * Shortcut of {@link SQLStatementExecutor#execute(Query, SQLResultAdapter)} with SQLResultAdapter is
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
        return executor().execute(select, DSLAdapter.fetchOne(select.asTable()));
    }

    /**
     * Shortcut of {@link SQLStatementExecutor#execute(Function, SQLResultAdapter, Handler)} with SQLResultAdapter is
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
     * Shortcut of {@link SQLStatementExecutor#execute(Query, SQLResultAdapter, Handler)} with SQLResultAdapter is
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
        return executor().execute(select, DSLAdapter.fetchMany(select.asTable()));
    }

    /**
     * Shortcut of {@link SQLStatementExecutor#execute(Function, SQLResultAdapter, Handler)} with SQLResultAdapter is
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
     * Shortcut of {@link SQLStatementExecutor#execute(Query, SQLResultAdapter, Handler)} with SQLResultAdapter is
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
        return executor().execute(DSL.selectOne().where(DSL.exists(select)), DSLAdapter.fetchExists());
    }

    /**
     * Shortcut of {@link SQLStatementExecutor#execute(Function, SQLResultAdapter, Handler)} with SQLResultAdapter is
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
     * Shortcut of {@link SQLStatementExecutor#execute(Query, SQLResultAdapter, Handler)} with SQLResultAdapter is
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
        return executor().execute(DSL.selectCount().from(select), DSLAdapter.fetchCount());
    }

    /**
     * Shortcut of {@link SQLStatementExecutor#execute(Query, SQLResultAdapter, Handler)} with SQLResultAdapter is
     * {@link DSLAdapter#fetchJsonRecord(TableLike)}
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
    default <REC extends Record> void fetchJsonRecord(@NotNull Function<DSLContext, Select<REC>> selectFn,
                                                      @NotNull Handler<AsyncResult<JsonRecord<REC>>> handler) {
        fetchJsonRecord(selectFn).onComplete(handler);
    }

    /**
     * Like {@link #fetchJsonRecord(Function, Handler)} but returns a {@code Future} of the asynchronous result
     *
     * @param selectFn the jOOQ select
     * @param <REC>    Type of Record
     * @apiNote Unfortunately, it is not support {@code rxify/mutiny} version due to technical problem in
     *     {@code Vert.x} code generator
     * @see Select
     * @since 2.0.0
     */
    @GenIgnore
    default <REC extends Record> Future<JsonRecord<REC>> fetchJsonRecord(
        @NotNull Function<DSLContext, Select<REC>> selectFn) {
        return fetchJsonRecord(selectFn.apply(dsl()));
    }

    /**
     * Shortcut of {@link SQLStatementExecutor#execute(Query, SQLResultAdapter, Handler)} with SQLResultAdapter is
     * {@link DSLAdapter#fetchJsonRecord(TableLike)}
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
    default <REC extends Record> void fetchJsonRecord(@NotNull Select<REC> select,
                                                      @NotNull Handler<AsyncResult<JsonRecord<REC>>> handler) {
        fetchJsonRecord(select).onComplete(handler);
    }

    /**
     * Like {@link #fetchJsonRecord(Select, Handler)} but returns a {@code Future} of the asynchronous result
     *
     * @param select the jOOQ select
     * @param <REC>  Type of Record
     * @apiNote Unfortunately, it is not support {@code rxify/mutiny} version due to technical problem in
     *     {@code Vert.x} code generator
     * @see Select
     * @since 2.0.0
     */
    @GenIgnore
    default <REC extends Record> Future<JsonRecord<REC>> fetchJsonRecord(@NotNull Select<REC> select) {
        return executor().execute(select, DSLAdapter.fetchJsonRecord(select.asTable()));
    }

    /**
     * Shortcut of {@link SQLStatementExecutor#execute(Query, SQLResultAdapter, Handler)} with SQLResultAdapter is
     * {@link DSLAdapter#fetchJsonRecords(TableLike)}
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
    default <REC extends Record> void fetchJsonRecords(@NotNull Function<DSLContext, Select<REC>> selectFn,
                                                       @NotNull Handler<AsyncResult<List<JsonRecord<REC>>>> handler) {
        fetchJsonRecords(selectFn).onComplete(handler);
    }

    /**
     * Like {@link #fetchJsonRecords(Function, Handler)} but returns a {@code Future} of the asynchronous result
     *
     * @param selectFn the jOOQ select function
     * @param <REC>    Type of Record
     * @apiNote Unfortunately, it is not support {@code rxify/mutiny} version due to technical problem in
     *     {@code Vert.x} code generator
     * @see Select
     * @since 2.0.0
     */
    @GenIgnore
    default <REC extends Record> Future<List<JsonRecord<REC>>> fetchJsonRecords(
        @NotNull Function<DSLContext, Select<REC>> selectFn) {
        return fetchJsonRecords(selectFn.apply(dsl()));
    }

    /**
     * Shortcut of {@link SQLStatementExecutor#execute(Query, SQLResultAdapter, Handler)} with SQLResultAdapter is
     * {@link DSLAdapter#fetchJsonRecords(TableLike)}
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
    default <REC extends Record> void fetchJsonRecords(@NotNull Select<REC> select,
                                                       @NotNull Handler<AsyncResult<List<JsonRecord<REC>>>> handler) {
        fetchJsonRecords(select).onComplete(handler);
    }

    /**
     * Like {@link #fetchJsonRecords(Select, Handler)} but returns a {@code Future} of the asynchronous result
     *
     * @param select the jOOQ select
     * @param <REC>  Type of Record
     * @apiNote Unfortunately, it is not support {@code rxify/mutiny} version due to technical problem in
     *     {@code Vert.x} code generator
     * @see Select
     * @since 2.0.0
     */
    @GenIgnore
    default <REC extends Record> Future<List<JsonRecord<REC>>> fetchJsonRecords(@NotNull Select<REC> select) {
        return executor().execute(select, DSLAdapter.fetchJsonRecords(select.asTable()));
    }

    /**
     * Shortcut of {@link SQLStatementExecutor#execute(Query, SQLResultAdapter, Handler)} with SQLResultAdapter is
     * {@link DSLAdapter#fetchJsonObject(TableLike)}
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
    default <REC extends Record> void fetchJsonObject(@NotNull Function<DSLContext, Select<REC>> selectFn,
                                                      @NotNull Handler<AsyncResult<JsonObject>> handler) {
        fetchJsonObject(selectFn).onComplete(handler);
    }

    /**
     * Like {@link #fetchJsonObject(Function, Handler)} but returns a {@code Future} of the asynchronous result
     *
     * @param selectFn the jOOQ select
     * @param <REC>    Type of Record
     * @apiNote Unfortunately, it is not support {@code rxify/mutiny} version due to technical problem in
     *     {@code Vert.x} code generator
     * @see Select
     * @since 2.0.0
     */
    @GenIgnore
    default <REC extends Record> Future<JsonObject> fetchJsonObject(
        @NotNull Function<DSLContext, Select<REC>> selectFn) {
        return fetchJsonObject(selectFn.apply(dsl()));
    }

    /**
     * Shortcut of {@link SQLStatementExecutor#execute(Query, SQLResultAdapter, Handler)} with SQLResultAdapter is
     * {@link DSLAdapter#fetchJsonObject(TableLike)}
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
    default <REC extends Record> void fetchJsonObject(@NotNull Select<REC> select,
                                                      @NotNull Handler<AsyncResult<JsonObject>> handler) {
        fetchJsonObject(select).onComplete(handler);
    }

    /**
     * Like {@link #fetchJsonObject(Select, Handler)} but returns a {@code Future} of the asynchronous result
     *
     * @param select the jOOQ select
     * @param <REC>  Type of Record
     * @apiNote Unfortunately, it is not support {@code rxify/mutiny} version due to technical problem in
     *     {@code Vert.x} code generator
     * @see Select
     * @since 2.0.0
     */
    @GenIgnore
    default <REC extends Record> Future<JsonObject> fetchJsonObject(@NotNull Select<REC> select) {
        return executor().execute(select, DSLAdapter.fetchJsonObject(select.asTable()));
    }

    /**
     * Shortcut of {@link SQLStatementExecutor#execute(Query, SQLResultAdapter, Handler)} with SQLResultAdapter is
     * {@link DSLAdapter#fetchJsonObject(TableLike, JsonCodec)}
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
    default <REC extends Record> void fetchJsonObject(@NotNull Function<DSLContext, Select<REC>> selectFn,
                                                      @NotNull JsonCodec codec,
                                                      @NotNull Handler<AsyncResult<JsonObject>> handler) {
        fetchJsonObject(selectFn, codec).onComplete(handler);
    }

    /**
     * Like {@link #fetchJsonObject(Function, JsonCodec, Handler)} but returns a {@code Future} of the asynchronous
     * result
     *
     * @param selectFn the jOOQ select
     * @param <REC>    Type of Record
     * @apiNote Unfortunately, it is not support {@code rxify/mutiny} version due to technical problem in
     *     {@code Vert.x} code generator
     * @see Select
     * @since 2.0.0
     */
    @GenIgnore
    default <REC extends Record> Future<JsonObject> fetchJsonObject(@NotNull Function<DSLContext, Select<REC>> selectFn,
                                                                    @NotNull JsonCodec codec) {
        return fetchJsonObject(selectFn.apply(dsl()), codec);
    }

    /**
     * Shortcut of {@link SQLStatementExecutor#execute(Query, SQLResultAdapter, Handler)} with SQLResultAdapter is
     * {@link DSLAdapter#fetchJsonObject(TableLike, JsonCodec)}
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
    default <REC extends Record> void fetchJsonObject(@NotNull Select<REC> select, @NotNull JsonCodec codec,
                                                      @NotNull Handler<AsyncResult<JsonObject>> handler) {
        fetchJsonObject(select, codec).onComplete(handler);
    }

    /**
     * Like {@link #fetchJsonObject(Select, JsonCodec, Handler)} but returns a {@code Future} of the asynchronous result
     *
     * @param select the jOOQ select
     * @param <REC>  Type of Record
     * @apiNote Unfortunately, it is not support {@code rxify/mutiny} version due to technical problem in
     *     {@code Vert.x} code generator
     * @see Select
     * @since 2.0.0
     */
    @GenIgnore
    default <REC extends Record> Future<JsonObject> fetchJsonObject(@NotNull Select<REC> select,
                                                                    @NotNull JsonCodec codec) {
        return executor().execute(select, DSLAdapter.fetchJsonObject(select.asTable(), codec));
    }

    /**
     * Shortcut of {@link SQLStatementExecutor#execute(Query, SQLResultAdapter, Handler)} with SQLResultAdapter is
     * {@link DSLAdapter#fetchJsonArray(TableLike)}
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
    default <REC extends Record> void fetchJsonArray(@NotNull Function<DSLContext, Select<REC>> selectFn,
                                                     @NotNull Handler<AsyncResult<JsonArray>> handler) {
        fetchJsonArray(selectFn).onComplete(handler);
    }

    /**
     * Like {@link #fetchJsonArray(Function, Handler)} but returns a {@code Future} of the asynchronous result
     *
     * @param selectFn the jOOQ select
     * @param <REC>    Type of Record
     * @apiNote Unfortunately, it is not support {@code rxify/mutiny} version due to technical problem in
     *     {@code Vert.x} code generator
     * @see Select
     * @since 2.0.0
     */
    @GenIgnore
    default <REC extends Record> Future<JsonArray> fetchJsonArray(@NotNull Function<DSLContext, Select<REC>> selectFn) {
        return fetchJsonArray(selectFn.apply(dsl()));
    }

    /**
     * Shortcut of {@link SQLStatementExecutor#execute(Query, SQLResultAdapter, Handler)} with SQLResultAdapter is
     * {@link DSLAdapter#fetchJsonArray(TableLike)}
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
    default <REC extends Record> void fetchJsonArray(@NotNull Select<REC> select,
                                                     @NotNull Handler<AsyncResult<JsonArray>> handler) {
        fetchJsonArray(select).onComplete(handler);
    }

    /**
     * Like {@link #fetchJsonArray(Select, Handler)} but returns a {@code Future} of the asynchronous result
     *
     * @param select the jOOQ select
     * @param <REC>  Type of Record
     * @apiNote Unfortunately, it is not support {@code rxify/mutiny} version due to technical problem in
     *     {@code Vert.x} code generator
     * @see Select
     * @since 2.0.0
     */
    @GenIgnore
    default <REC extends Record> Future<JsonArray> fetchJsonArray(@NotNull Select<REC> select) {
        return executor().execute(select, DSLAdapter.fetchJsonArray(select.asTable()));
    }

    /**
     * Shortcut of {@link SQLStatementExecutor#execute(Query, SQLResultAdapter, Handler)} with SQLResultAdapter is
     * {@link DSLAdapter#fetchJsonArray(TableLike, JsonCodec)}
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
    default <REC extends Record> void fetchJsonArray(@NotNull Function<DSLContext, Select<REC>> selectFn,
                                                     @NotNull JsonCodec codec,
                                                     @NotNull Handler<AsyncResult<JsonArray>> handler) {
        fetchJsonArray(selectFn, codec).onComplete(handler);
    }

    /**
     * Like {@link #fetchJsonArray(Function, JsonCodec, Handler)} but returns a {@code Future} of the asynchronous
     * result
     *
     * @param selectFn the jOOQ select
     * @param <REC>    Type of Record
     * @apiNote Unfortunately, it is not support {@code rxify/mutiny} version due to technical problem in
     *     {@code Vert.x} code generator
     * @see Select
     * @since 2.0.0
     */
    @GenIgnore
    default <REC extends Record> Future<JsonArray> fetchJsonArray(@NotNull Function<DSLContext, Select<REC>> selectFn,
                                                                  @NotNull JsonCodec codec) {
        return fetchJsonArray(selectFn.apply(dsl()), codec);
    }

    /**
     * Shortcut of {@link SQLStatementExecutor#execute(Query, SQLResultAdapter, Handler)} with SQLResultAdapter is
     * {@link DSLAdapter#fetchJsonArray(TableLike, JsonCodec)}
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
    default <REC extends Record> void fetchJsonArray(@NotNull Select<REC> select, @NotNull JsonCodec codec,
                                                     @NotNull Handler<AsyncResult<JsonArray>> handler) {
        fetchJsonArray(select, codec).onComplete(handler);
    }

    /**
     * Like {@link #fetchJsonArray(Select, JsonCodec, Handler)} but returns a {@code Future} of the asynchronous result
     *
     * @param select the jOOQ select
     * @param <REC>  Type of Record
     * @apiNote Unfortunately, it is not support {@code rxify/mutiny} version due to technical problem in
     *     {@code Vert.x} code generator
     * @see Select
     * @since 2.0.0
     */
    @GenIgnore
    default <REC extends Record> Future<JsonArray> fetchJsonArray(@NotNull Select<REC> select,
                                                                  @NotNull JsonCodec codec) {
        return executor().execute(select, DSLAdapter.fetchJsonArray(select.asTable(), codec));
    }

}
