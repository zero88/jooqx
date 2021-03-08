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
import io.vertx.sqlclient.SqlConnection;

public interface PostgreSQLReactiveTest extends PostgreSQLDBProvider,
                                                BaseReactiveSqlTest<PostgreSQLContainer<?>,
                                                                       DBContainerProvider<PostgreSQLContainer<?>>> {

    @Override
    default SqlClient createConnection(Vertx vertx, VertxTestContext ctx, SqlConnectionOption connOpt) {
        Checkpoint async = ctx.checkpoint();
        SqlConnection[] connections = new SqlConnection[1];
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

    @Override
    default SqlClient createPool(Vertx vertx, VertxTestContext ctx, SqlConnectionOption opt) {
        final PgPool pool = PgPool.pool(connectionOptions(opt), poolOptions());
        ctx.completeNow();
        return pool;
    }

    default PgConnectOptions connectionOptions(SqlConnectionOption server) {
        return new PgConnectOptions().setHost(server.getHost())
                                     .setPort(server.getPort())
                                     .setDatabase(server.getDatabase())
                                     .setUser(server.getUsername())
                                     .setPassword(server.getPassword());
    }

    abstract class AbstractPostgreSQLReactiveTest extends AbstractReactiveDBCTest<PostgreSQLContainer<?>>
        implements PostgreSQLReactiveTest {

    }

}
