package io.zero88.jooqx.spi;

import java.util.concurrent.TimeUnit;

import org.testcontainers.containers.PostgreSQLContainer;

import io.zero88.jooqx.DBProvider.DBContainerProvider;
import io.zero88.jooqx.ReactiveSQLTest;
import io.zero88.jooqx.SQLConnectionOption;
import io.vertx.core.Vertx;
import io.vertx.junit5.Checkpoint;
import io.vertx.junit5.VertxTestContext;
import io.vertx.pgclient.PgConnectOptions;
import io.vertx.pgclient.PgConnection;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.SqlClient;

public interface PostgreSQLReactiveTest<S extends SqlClient> extends PostgreSQLDBProvider,
                                                                     ReactiveSQLTest<S, PostgreSQLContainer<?>,
                                                                                        DBContainerProvider<PostgreSQLContainer<?>>> {

    default PgConnectOptions connectionOptions(SQLConnectionOption server) {
        return new PgConnectOptions().setHost(server.getHost())
                                     .setPort(server.getPort())
                                     .setDatabase(server.getDatabase())
                                     .setUser(server.getUsername())
                                     .setPassword(server.getPassword());
    }

    abstract class PostgreSQLClientTest extends ReactiveDBContainerTest<PgConnection, PostgreSQLContainer<?>>
        implements PostgreSQLReactiveTest<PgConnection> {

        @Override
        public PgConnection createSqlClient(Vertx vertx, VertxTestContext ctx, SQLConnectionOption connOpt) {
            Checkpoint async = ctx.checkpoint();
            PgConnection[] connections = new PgConnection[1];
            PgConnection.connect(vertx, connectionOptions(connOpt), ctx.succeeding(conn -> {
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


    abstract class PostgreSQLPoolTest extends ReactiveDBContainerTest<PgPool, PostgreSQLContainer<?>>
        implements PostgreSQLReactiveTest<PgPool> {

        @Override
        public PgPool createSqlClient(Vertx vertx, VertxTestContext ctx, SQLConnectionOption connOpt) {
            final PgPool pool = PgPool.pool(vertx, connectionOptions(connOpt), poolOptions());
            ctx.completeNow();
            return pool;
        }

    }

}
