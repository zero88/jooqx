package io.github.zero88.jooq.vertx.spi;

import java.util.concurrent.TimeUnit;

import org.testcontainers.containers.PostgreSQLContainer;

import io.github.zero88.jooq.vertx.BaseReactiveSqlTest;
import io.github.zero88.jooq.vertx.DBProvider.DBContainerProvider;
import io.github.zero88.jooq.vertx.SqlConnectionOption;
import io.vertx.core.Vertx;
import io.vertx.junit5.Checkpoint;
import io.vertx.junit5.VertxTestContext;
import io.vertx.pgclient.PgConnectOptions;
import io.vertx.pgclient.PgConnection;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.SqlClient;

public interface PostgreSQLReactiveTest<S extends SqlClient> extends PostgreSQLDBProvider,
                                                                     BaseReactiveSqlTest<S, PostgreSQLContainer<?>,
                                                                                            DBContainerProvider<PostgreSQLContainer<?>>> {

    default PgConnectOptions connectionOptions(SqlConnectionOption server) {
        return new PgConnectOptions().setHost(server.getHost())
                                     .setPort(server.getPort())
                                     .setDatabase(server.getDatabase())
                                     .setUser(server.getUsername())
                                     .setPassword(server.getPassword());
    }

    abstract class AbstractPostgreSQLClientTest extends AbstractReactiveDBCTest<PgConnection, PostgreSQLContainer<?>>
        implements PostgreSQLReactiveTest<PgConnection> {

        @Override
        public PgConnection createSqlClient(Vertx vertx, VertxTestContext ctx, SqlConnectionOption connOpt) {
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


    abstract class AbstractPostgreSQLPoolTest extends AbstractReactiveDBCTest<PgPool, PostgreSQLContainer<?>>
        implements PostgreSQLReactiveTest<PgPool> {

        @Override
        public PgPool createSqlClient(Vertx vertx, VertxTestContext ctx, SqlConnectionOption connOpt) {
            final PgPool pool = PgPool.pool(vertx, connectionOptions(connOpt), poolOptions());
            ctx.completeNow();
            return pool;
        }

    }

}
