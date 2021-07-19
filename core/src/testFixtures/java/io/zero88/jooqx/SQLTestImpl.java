package io.zero88.jooqx;

import java.util.concurrent.TimeUnit;

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
import lombok.SneakyThrows;

@ExtendWith(VertxExtension.class)
abstract class SQLTestImpl<S, B, P extends SQLPreparedQuery<B>, R, C extends SQLResultCollector<R>,
                              E extends SQLExecutor<S, B, P, R, C>, K, D extends DBProvider<K>>
    implements SQLTest<S, B, P, R, C, E, K, D> {

    public static int TIMEOUT_IN_SECOND = 10;
    protected E jooqx;
    protected SQLConnectionOption connOpt;

    @BeforeAll
    public static void setup() {
        System.setProperty("vertx.logger-delegate-factory-class-name", "io.vertx.core.logging.SLF4JLogDelegateFactory");
        ((Logger) LoggerFactory.getLogger("ROOT")).setLevel(Level.INFO);
        ((Logger) LoggerFactory.getLogger("io.vertx.sqlclient")).setLevel(Level.DEBUG);
        ((Logger) LoggerFactory.getLogger("org.jooq")).setLevel(Level.DEBUG);
        ((Logger) LoggerFactory.getLogger(SQLTest.class.getPackage().getName())).setLevel(Level.DEBUG);
    }

    @SneakyThrows
    @BeforeEach
    public void tearUp(Vertx vertx, VertxTestContext ctx) {
        connOpt = dbProvider().connOpt(getDB());
        clientProvider().open(vertx, connOpt.toJson())
                        .map(s -> jooqxProvider().createExecutor(vertx, dslProvider(), s))
                        .onSuccess(jooqx -> this.jooqx = jooqx)
                        .onComplete(ctx.succeedingThenComplete());
        if (ctx.awaitCompletion(TIMEOUT_IN_SECOND, TimeUnit.SECONDS)) {
            ctx.failNow("Timeout when open SQL connection");
        }
        System.out.println(Strings.duplicate("=", 150));
    }

    @AfterEach
    public void tearDown(Vertx vertx, VertxTestContext ctx) {
        clientProvider().close().onComplete(ctx.succeedingThenComplete());
    }

    @Override
    public S sqlClient() {
        return jooqx.sqlClient();
    }

    protected abstract K getDB();

    @Testcontainers
    abstract static class DBContainerSQLTest<S, B, P extends SQLPreparedQuery<B>, R, C extends SQLResultCollector<R>,
                                                    E extends SQLExecutor<S, B, P, R, C>,
                                                    K extends JdbcDatabaseContainer<?>>
        extends SQLTestImpl<S, B, P, R, C, E, K, DBContainerProvider<K>> {

        @Container
        protected K db = dbProvider().get();

        @Override
        protected K getDB() {
            return db;
        }

    }


    abstract static class DBMemorySQLTest<S, B, P extends SQLPreparedQuery<B>, R, C extends SQLResultCollector<R>,
                                             E extends SQLExecutor<S, B, P, R, C>>
        extends SQLTestImpl<S, B, P, R, C, E, String, DBMemoryProvider> {

        @Override
        protected String getDB() {
            return dbProvider().get();
        }

    }

}
