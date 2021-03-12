package io.zero88.jooqx;

import java.util.List;
import java.util.Objects;

import org.jooq.exception.DataAccessException;
import org.jooq.exception.SQLStateClass;
import org.junit.jupiter.api.Assertions;

import io.github.zero88.utils.Strings;
import io.vertx.core.AsyncResult;
import io.vertx.junit5.Checkpoint;
import io.vertx.junit5.VertxTestContext;

import com.zaxxer.hikari.HikariDataSource;

public interface SQLTestHelper {

    /**
     * Prepare database schema and data by plain JDBC connection. That use HikariDataSource
     *
     * @param context    test context
     * @param jooqSql    jooqSQL
     * @param connOption database connection option
     * @param files      resource SQL files
     */
    default void prepareDatabase(VertxTestContext context, JooqSQL<?> jooqSql, SQLConnectionOption connOption,
                                 String... files) {
        HikariDataSource dataSource = jooqSql.createDataSource(connOption);
        jooqSql.prepareDatabase(context, jooqSql.dsl(dataSource), files);
        jooqSql.closeDataSource(dataSource);
        System.out.println(Strings.duplicate("=", 150));
    }

    /**
     * Assert async result is completed successful
     *
     * @param <T>         type of output
     * @param context     test context
     * @param asyncResult async result
     * @return output
     */
    default <T> T assertSuccess(VertxTestContext context, AsyncResult<T> asyncResult) {
        Checkpoint flag = context.checkpoint();
        context.verify(() -> Assertions.assertTrue(asyncResult.succeeded()));
        if (Objects.nonNull(asyncResult.cause())) {
            asyncResult.cause().printStackTrace();
        }
        flag.flag();
        return asyncResult.result();
    }

    /**
     * Assert async result is completed successful then compare result size
     *
     * @param context     test context
     * @param asyncResult async result
     * @param expected    expected size
     * @param <R>         type of output
     * @return list of output
     */
    default <R> List<R> assertResultSize(VertxTestContext context, AsyncResult<List<R>> asyncResult, int expected) {
        Checkpoint flag = context.checkpoint();
        final List<R> records = assertSuccess(context, asyncResult);
        System.out.println(records);
        context.verify(() -> Assertions.assertEquals(expected, records.size()));
        flag.flag();
        return records;
    }

    /**
     * Assert async result is completed with error then compare exception
     *
     * @param context    test context
     * @param ar         async result
     * @param stateClass An expectation SQL state class
     * @param <X>        type of result
     * @see SQLStateClass
     */
    default <X> void assertJooqException(VertxTestContext context, AsyncResult<X> ar, SQLStateClass stateClass) {
        Checkpoint flag = context.checkpoint();
        context.verify(() -> assertJooqException(stateClass, ar.failed(), ar.cause()));
        flag.flag();
    }

    /**
     * Assert async result is completed with error then compare exception
     *
     * @param context    test context
     * @param ar         async result
     * @param stateClass An expectation SQL state class
     * @param errorMsg   An expectation error message
     * @param <X>        type of result
     */
    default <X> void assertJooqException(VertxTestContext context, AsyncResult<X> ar, SQLStateClass stateClass,
                                         String errorMsg) {
        Checkpoint flag = context.checkpoint();
        context.verify(() -> {
            assertJooqException(stateClass, ar.failed(), ar.cause());
            Assertions.assertEquals(errorMsg, ar.cause().getMessage());
        });
        flag.flag();
    }

    /**
     * Assert async result is completed with error then compare exception
     *
     * @param context    test context
     * @param ar         async result
     * @param stateClass An expectation SQL state class
     * @param errorMsg   An expectation error message
     * @param causeType  An expectation cause type
     * @param <X>        type of result
     */
    default <X> void assertJooqException(VertxTestContext context, AsyncResult<X> ar, SQLStateClass stateClass,
                                         String errorMsg, Class<? extends Throwable> causeType) {
        Checkpoint flag = context.checkpoint();
        context.verify(() -> {
            assertJooqException(stateClass, ar.failed(), ar.cause());
            Assertions.assertEquals(errorMsg, ar.cause().getMessage());
            Assertions.assertNotNull(((DataAccessException) ar.cause()).getCause(causeType));
        });
        flag.flag();
    }

    /**
     * Internal compare. Don't use it directly
     */
    default void assertJooqException(SQLStateClass stateClass, boolean failed, Throwable cause) {
        Assertions.assertTrue(failed);
        Assertions.assertTrue(cause instanceof DataAccessException);
        Assertions.assertEquals(stateClass, ((DataAccessException) cause).sqlStateClass());
    }

}
