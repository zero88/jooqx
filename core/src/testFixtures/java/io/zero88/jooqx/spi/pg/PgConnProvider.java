package io.zero88.jooqx.spi.pg;

import java.util.concurrent.TimeUnit;

import io.vertx.core.Vertx;
import io.vertx.core.tracing.TracingPolicy;
import io.vertx.junit5.Checkpoint;
import io.vertx.junit5.VertxTestContext;
import io.vertx.pgclient.PgConnection;
import io.zero88.jooqx.ReactiveTestDefinition.ReactiveSQLClientProvider;
import io.zero88.jooqx.SQLConnectionOption;

public interface PgConnProvider extends ReactiveSQLClientProvider<PgConnection>, PgSQLClientProvider {

    @Override
    default PgConnection createSqlClient(Vertx vertx, VertxTestContext ctx, SQLConnectionOption connOpt) {
        Checkpoint async = ctx.checkpoint();
        PgConnection[] connections = new PgConnection[1];
        PgConnection.connect(vertx,
                             connectionOptions(connOpt).setTracingPolicy(TracingPolicy.ALWAYS).setLogActivity(true),
                             ctx.succeeding(conn -> {
                                 connections[0] = conn;
                                 async.flag();
                             }));
        try {
            ctx.awaitCompletion(10, TimeUnit.SECONDS);
        } catch (Exception ex) {
            ctx.failNow(ex);
        }
        return connections[0];
    }

}
