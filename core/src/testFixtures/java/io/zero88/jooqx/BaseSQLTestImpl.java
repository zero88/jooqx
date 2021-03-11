package io.zero88.jooqx;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import io.github.zero88.utils.Strings;
import io.vertx.core.Vertx;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import io.zero88.jooqx.DBProvider.DBContainerProvider;
import io.zero88.jooqx.DBProvider.DBMemoryProvider;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;

@ExtendWith(VertxExtension.class)
public abstract class BaseSQLTestImpl<S, P, R, E extends SQLExecutor<S, P, R>, K, D extends DBProvider<K>>
    implements BaseSQLTest<S, P, R, E, K, D> {

    protected E executor;
    protected SQLConnectionOption connOpt;

    @BeforeAll
    public static void setup() {
        System.setProperty("vertx.logger-delegate-factory-class-name", "io.vertx.core.logging.SLF4JLogDelegateFactory");
        ((Logger) LoggerFactory.getLogger("ROOT")).setLevel(Level.INFO);
        ((Logger) LoggerFactory.getLogger("io.github.zero88")).setLevel(Level.DEBUG);
        ((Logger) LoggerFactory.getLogger("io.vertx.sqlclient")).setLevel(Level.DEBUG);
        ((Logger) LoggerFactory.getLogger("org.jooq")).setLevel(Level.DEBUG);
    }

    @BeforeEach
    public void tearUp(Vertx vertx, VertxTestContext ctx) {
        connOpt = dbProvider().connOpt(getDB());
        executor = executorProvider().createExecutor(vertx, dslProvider(),
                                                     clientProvider().createSqlClient(vertx, ctx, connOpt));
        System.out.println(Strings.duplicate("=", 150));
    }

    @AfterEach
    public void tearDown(Vertx vertx, VertxTestContext ctx) {
        clientProvider().closeClient(ctx);
    }

    @Override
    public S sqlClient() {
        return executor.sqlClient();
    }

    protected abstract K getDB();

    @Testcontainers
    public abstract static class DBContainerSQLTest<S, P, R, E extends SQLExecutor<S, P, R>,
                                                                K extends JdbcDatabaseContainer<?>>
        extends BaseSQLTestImpl<S, P, R, E, K, DBContainerProvider<K>> {

        @Container
        protected K db = dbProvider().get();

        @Override
        protected K getDB() {
            return db;
        }

    }


    public abstract static class DBMemorySQLTest<S, P, R, E extends SQLExecutor<S, P, R>>
        extends BaseSQLTestImpl<S, P, R, E, String, DBMemoryProvider> {

        @Override
        protected String getDB() {
            return dbProvider().get();
        }

    }

}
