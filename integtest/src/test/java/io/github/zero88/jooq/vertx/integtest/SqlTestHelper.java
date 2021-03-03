package io.github.zero88.jooq.vertx.integtest;

import java.util.List;

import org.junit.jupiter.api.Assertions;

import io.vertx.core.AsyncResult;
import io.vertx.junit5.Checkpoint;
import io.vertx.junit5.VertxTestContext;

public interface SqlTestHelper {

    default <R> List<R> assertRsSize(VertxTestContext ctx, Checkpoint flag, AsyncResult<List<R>> asyncResult,
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

}
