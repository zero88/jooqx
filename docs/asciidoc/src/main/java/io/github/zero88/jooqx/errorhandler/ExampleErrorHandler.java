package io.github.zero88.jooqx.errorhandler;

import org.jooq.DSLContext;

import io.github.zero88.jooqx.JooqErrorConverter.JDBCErrorConverter;
import io.github.zero88.jooqx.Jooqx;
import io.github.zero88.jooqx.SQLErrorConverter;
import io.github.zero88.jooqx.spi.pg.PgErrorConverter;
import io.vertx.core.Vertx;
import io.vertx.docgen.Source;
import io.vertx.jdbcclient.JDBCPool;
import io.vertx.pgclient.PgPool;

@Source
class ExampleErrorHandler {

    void jdbcErrorHandler(Vertx vertx, DSLContext dslContext, JDBCPool pool) {
        Jooqx jooqx = Jooqx.builder()
                           .setVertx(vertx)
                           .setDSL(dslContext)
                           .setSqlClient(pool)
                           .setErrorConverter(new JDBCErrorConverter())
                           .build();
    }

    void pgErrorHandler(Vertx vertx, DSLContext dslContext, PgPool pool) {
        Jooqx jooqx = Jooqx.builder()
                           .setVertx(vertx)
                           .setDSL(dslContext)
                           .setSqlClient(pool)
                           .setErrorConverter(new PgErrorConverter())
                           .build();
    }

    // @formatter:off
    void integrate(Vertx vertx, DSLContext dslContext, JDBCPool pool) {
        SQLErrorConverter errorConverter = new JDBCErrorConverter()
            .andThen(dataAccessException -> new YourAppError(ErrorCode.DUPLICATE, dataAccessException));
        Jooqx jooqx = Jooqx.builder()
                           .setVertx(vertx)
                           .setDSL(dslContext)
                           .setSqlClient(pool)
                           .setErrorConverter(errorConverter)
                           .build();
    }// @formatter:on

    enum ErrorCode {
        DUPLICATE
    }


    class YourAppError extends RuntimeException {

        final ErrorCode errorCode;

        YourAppError(ErrorCode errorCode, Exception rootCause) {
            super(rootCause);
            this.errorCode = errorCode;
        }

    }

}
