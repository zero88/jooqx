package io.zero88.jooqx;

import java.util.List;

import org.jooq.exception.DataAccessException;
import org.jooq.exception.SQLStateClass;
import org.junit.jupiter.api.Assertions;

import io.github.zero88.utils.Strings;
import io.vertx.core.AsyncResult;
import io.vertx.junit5.Checkpoint;
import io.vertx.junit5.VertxTestContext;

import com.zaxxer.hikari.HikariDataSource;

public interface SQLTestHelper {

    default void prepareDatabase(VertxTestContext ctx, JooqSQL<?> jooqSql, SQLConnectionOption connOption,
                                 String... files) {
        HikariDataSource dataSource = jooqSql.createDataSource(connOption);
        jooqSql.prepareDatabase(ctx, jooqSql.dsl(dataSource), files);
        jooqSql.closeDataSource(dataSource);
        System.out.println(Strings.duplicate("=", 150));
    }

    default <R> List<R> assertResultSize(VertxTestContext ctx, Checkpoint flag, AsyncResult<List<R>> asyncResult,
                                         int expected) {
        try {
            if (asyncResult.succeeded()) {
                final List<R> records = asyncResult.result();
                System.out.println(records);
                ctx.verify(() -> Assertions.assertEquals(records.size(), expected));
                return records;
            } else {
                ctx.failNow(asyncResult.cause());
                return null;
            }
        } finally {
            flag.flag();
        }
    }

    default <X> void assertJooqException(VertxTestContext ctx, Checkpoint flag, AsyncResult<X> ar,
                                         SQLStateClass stateClass) {
        ctx.verify(() -> assertJooqException(stateClass, ar.failed(), ar.cause()));
        flag.flag();
    }

    default <X> void assertJooqException(VertxTestContext ctx, Checkpoint flag, AsyncResult<X> ar,
                                         SQLStateClass stateClass, String errorMsg) {
        ctx.verify(() -> {
            assertJooqException(stateClass, ar.failed(), ar.cause());
            Assertions.assertEquals(errorMsg, ar.cause().getMessage());
        });
        flag.flag();
    }

    default <X> void assertJooqException(VertxTestContext ctx, Checkpoint flag, AsyncResult<X> ar,
                                         SQLStateClass stateClass, String errorMsg,
                                         Class<? extends Throwable> causeType) {
        ctx.verify(() -> {
            assertJooqException(stateClass, ar.failed(), ar.cause());
            Assertions.assertEquals(errorMsg, ar.cause().getMessage());
            Assertions.assertNotNull(((DataAccessException) ar.cause()).getCause(causeType));
        });
        flag.flag();
    }

    default void assertJooqException(SQLStateClass stateClass, boolean failed, Throwable cause) {
        Assertions.assertTrue(failed);
        Assertions.assertTrue(cause instanceof DataAccessException);
        Assertions.assertEquals(stateClass, ((DataAccessException) cause).sqlStateClass());
    }

}
