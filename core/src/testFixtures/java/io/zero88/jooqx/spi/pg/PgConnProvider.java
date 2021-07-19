package io.zero88.jooqx.spi.pg;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.core.tracing.TracingPolicy;
import io.vertx.pgclient.PgConnection;
import io.zero88.jooqx.provider.ReactiveSQLClientProvider;

import lombok.NonNull;

public interface PgConnProvider extends ReactiveSQLClientProvider<PgConnection>, PgSQLClientProvider {

    @Override
    default @NonNull Future<PgConnection> open(Vertx vertx, JsonObject connOption) {
        return PgConnection.connect(vertx, connectionOptions(connOption).setTracingPolicy(TracingPolicy.ALWAYS)
                                                                        .setLogActivity(true));
    }

}
