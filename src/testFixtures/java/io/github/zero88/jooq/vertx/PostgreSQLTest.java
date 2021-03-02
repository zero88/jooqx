package io.github.zero88.jooq.vertx;

import java.util.concurrent.TimeUnit;

import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

import io.vertx.core.Vertx;
import io.vertx.ext.sql.SQLClient;
import io.vertx.junit5.Checkpoint;
import io.vertx.junit5.VertxTestContext;
import io.vertx.pgclient.PgConnectOptions;
import io.vertx.pgclient.PgConnection;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.PoolOptions;
import io.vertx.sqlclient.SqlClient;
import io.vertx.sqlclient.SqlConnection;

public interface PostgreSQLTest<T> extends ConnectionProvider<T> {

    DBContainerProvider<PostgreSQLContainer<?>> POSTGRES = new DBContainerProvider<PostgreSQLContainer<?>>() {

        @Override
        public PostgreSQLContainer<?> get() {
            return get("postgres:10-alpine");
        }

        @Override
        public PostgreSQLContainer<?> get(String imageName) {
            return new PostgreSQLContainer<>(DockerImageName.parse(imageName)).withDatabaseName("foo")
                                                                              .withUsername("foo")
                                                                              .withPassword("secret");
        }
    };

    @Override
    default DBContainerProvider<?> get() {
        return POSTGRES;
    }

    interface PostgreSQLReactiveTest extends PostgreSQLTest<SqlClient>, ReactiveSqlTest {

        @Override
        default SqlClient createConnection(Vertx vertx, VertxTestContext ctx, JdbcDatabaseContainer<?> server) {
            Checkpoint async = ctx.checkpoint();
            SqlConnection[] connections = new SqlConnection[1];
            PgConnection.connect(vertx, connectionOptions(server), ctx.succeeding(conn -> {
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
        default SqlClient createPool(Vertx vertx, VertxTestContext ctx, JdbcDatabaseContainer<?> server) {
            final PgPool pool = PgPool.pool(connectionOptions(server), poolOptions());
            ctx.completeNow();
            return pool;
        }

        default PgConnectOptions connectionOptions(JdbcDatabaseContainer<?> server) {
            return new PgConnectOptions().setHost(server.getHost())
                                         .setPort(server.getMappedPort(PostgreSQLContainer.POSTGRESQL_PORT))
                                         .setDatabase(server.getDatabaseName())
                                         .setUser(server.getUsername())
                                         .setPassword(server.getPassword());
        }

        default PoolOptions poolOptions() {
            return new PoolOptions();
        }

    }


    interface PostgreSQLJdbcTest extends PostgreSQLTest<SQLClient>, LegacyJdbcSqlTest {

    }

}
