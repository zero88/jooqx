package io.github.zero88.jooqx;

import java.util.function.Function;

import org.jetbrains.annotations.NotNull;
import org.jooq.DSLContext;
import org.jooq.Delete;
import org.jooq.DeleteQuery;
import org.jooq.Insert;
import org.jooq.InsertQuery;
import org.jooq.Record;
import org.jooq.Update;
import org.jooq.UpdateQuery;

import io.vertx.codegen.annotations.GenIgnore;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;

/**
 * Represents for a {@code DML executor} that executes {@code INSERT}, {@code UPDATE}, {@code DELETE} statements.
 *
 * @since 2.0.0
 */
@VertxGen(concrete = false)
public interface SQLDMLExecutor extends JooqDSLProvider, HasExecutor {

    @GenIgnore
    default <R extends Record> void insert(@NotNull Function<DSLContext, Insert<R>> insertFn,
                                           @NotNull Handler<AsyncResult<Integer>> handler) {
        insert(insertFn).onComplete(handler);
    }

    @GenIgnore
    default <R extends Record> Future<Integer> insert(@NotNull Function<DSLContext, Insert<R>> insertFn) {
        return insert(insertFn.apply(dsl()));
    }

    @GenIgnore
    default <R extends Record> void insert(@NotNull Insert<R> statement,
                                           @NotNull Handler<AsyncResult<Integer>> handler) {
        insert(statement).onComplete(handler);
    }

    /**
     * @param statement Insert statement
     * @return a {@code Future} of the asynchronous result
     * @since 2.0.0
     */
    @GenIgnore
    default <R extends Record> Future<Integer> insert(@NotNull Insert<R> statement) {
        return executor().execute(statement);
    }

    @GenIgnore
    default <R extends Record> void insertQuery(@NotNull Function<DSLContext, InsertQuery<R>> insertFn,
                                                @NotNull Handler<AsyncResult<Integer>> handler) {
        insertQuery(insertFn).onComplete(handler);
    }

    @GenIgnore
    default <R extends Record> Future<Integer> insertQuery(@NotNull Function<DSLContext, InsertQuery<R>> insertFn) {
        return insertQuery(insertFn.apply(dsl()));
    }

    @GenIgnore
    default <R extends Record> void insertQuery(@NotNull InsertQuery<R> statement,
                                                @NotNull Handler<AsyncResult<Integer>> handler) {
        insertQuery(statement).onComplete(handler);
    }

    @GenIgnore
    default <R extends Record> Future<Integer> insertQuery(@NotNull InsertQuery<R> statement) {
        return executor().execute(statement);
    }

    @GenIgnore
    default <R extends Record> void update(@NotNull Function<DSLContext, Update<R>> updateFn,
                                           @NotNull Handler<AsyncResult<Integer>> handler) {
        update(updateFn).onComplete(handler);
    }

    @GenIgnore
    default <R extends Record> Future<Integer> update(@NotNull Function<DSLContext, Update<R>> updateFn) {
        return update(updateFn.apply(dsl()));
    }

    @GenIgnore
    default <R extends Record> void update(@NotNull Update<R> statement,
                                           @NotNull Handler<AsyncResult<Integer>> handler) {
        update(statement).onComplete(handler);
    }

    /**
     * @param statement Update statement
     * @return a {@code Future} of the asynchronous result
     * @since 2.0.0
     */
    @GenIgnore
    default <R extends Record> Future<Integer> update(@NotNull Update<R> statement) {
        return executor().execute(statement);
    }

    @GenIgnore
    default <R extends Record> void updateQuery(@NotNull Function<DSLContext, UpdateQuery<R>> updateFn,
                                                @NotNull Handler<AsyncResult<Integer>> handler) {
        updateQuery(updateFn).onComplete(handler);
    }

    @GenIgnore
    default <R extends Record> Future<Integer> updateQuery(@NotNull Function<DSLContext, UpdateQuery<R>> updateFn) {
        return updateQuery(updateFn.apply(dsl()));
    }

    @GenIgnore
    default <R extends Record> void updateQuery(@NotNull UpdateQuery<R> statement,
                                                @NotNull Handler<AsyncResult<Integer>> handler) {
        updateQuery(statement).onComplete(handler);
    }

    /**
     * @param statement Update statement
     * @return a {@code Future} of the asynchronous result
     * @since 2.0.0
     */
    @GenIgnore
    default <R extends Record> Future<Integer> updateQuery(@NotNull UpdateQuery<R> statement) {
        return executor().execute(statement);
    }

    @GenIgnore
    default <R extends Record> void delete(@NotNull Function<DSLContext, Delete<R>> deleteFn,
                                           @NotNull Handler<AsyncResult<Integer>> handler) {
        delete(deleteFn).onComplete(handler);
    }

    @GenIgnore
    default <R extends Record> Future<Integer> delete(@NotNull Function<DSLContext, Delete<R>> deleteFn) {
        return delete(deleteFn.apply(dsl()));
    }

    @GenIgnore
    default <R extends Record> void delete(@NotNull Delete<R> statement,
                                           @NotNull Handler<AsyncResult<Integer>> handler) {
        delete(statement).onComplete(handler);
    }

    /**
     * @param statement Delete statement
     * @return a {@code Future} of the asynchronous result
     * @since 2.0.0
     */
    @GenIgnore
    default <R extends Record> Future<Integer> delete(@NotNull Delete<R> statement) {
        return executor().execute(statement);
    }

    @GenIgnore
    default <R extends Record> void deleteQuery(@NotNull Function<DSLContext, DeleteQuery<R>> deleteFn,
                                                @NotNull Handler<AsyncResult<Integer>> handler) {
        deleteQuery(deleteFn).onComplete(handler);
    }

    @GenIgnore
    default <R extends Record> Future<Integer> deleteQuery(@NotNull Function<DSLContext, DeleteQuery<R>> deleteFn) {
        return deleteQuery(deleteFn.apply(dsl()));
    }

    @GenIgnore
    default <R extends Record> void deleteQuery(@NotNull DeleteQuery<R> statement,
                                                @NotNull Handler<AsyncResult<Integer>> handler) {
        deleteQuery(statement).onComplete(handler);
    }

    /**
     * @param statement Delete statement
     * @return a {@code Future} of the asynchronous result
     * @since 2.0.0
     */
    @GenIgnore
    default <R extends Record> Future<Integer> deleteQuery(@NotNull DeleteQuery<R> statement) {
        return executor().execute(statement);
    }

}
