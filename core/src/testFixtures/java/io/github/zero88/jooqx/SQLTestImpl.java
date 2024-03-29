package io.github.zero88.jooqx;

import java.util.concurrent.TimeUnit;

import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import io.github.zero88.jooqx.provider.DBEmbeddedProvider.DBMemoryProvider;
import io.github.zero88.jooqx.provider.DBProvider;
import io.github.zero88.utils.Strings;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import io.vertx.sqlclient.PoolOptions;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;

@ExtendWith(VertxExtension.class)
abstract class SQLTestImpl<S, B, PQ extends SQLPreparedQuery<B>, RC extends SQLResultCollector,
                              E extends SQLExecutor<S, B, PQ, RC>, DB, DBP extends DBProvider<DB>>
    implements SQLTest<S, B, PQ, RC, E, DB, DBP> {

    public static int TIMEOUT_IN_SECOND = 10;
    protected E jooqx;
    protected SQLConnectionOption connOpt;

    @BeforeAll
    public static void setup() {
        System.setProperty("vertx.logger-delegate-factory-class-name", "io.vertx.core.logging.SLF4JLogDelegateFactory");
        ((Logger) LoggerFactory.getLogger("ROOT")).setLevel(Level.INFO);
        ((Logger) LoggerFactory.getLogger("io.vertx.sqlclient")).setLevel(Level.DEBUG);
        ((Logger) LoggerFactory.getLogger("io.vertx.ext.jdbc")).setLevel(Level.DEBUG);
        ((Logger) LoggerFactory.getLogger("org.jooq")).setLevel(Level.DEBUG);
        ((Logger) LoggerFactory.getLogger("org.testcontainers")).setLevel(Level.INFO);
        ((Logger) LoggerFactory.getLogger("com.github.dockerjava")).setLevel(Level.WARN);
        ((Logger) LoggerFactory.getLogger(SQLTest.class.getPackage().getName())).setLevel(Level.DEBUG);
    }

    @BeforeEach
    public void tearUp(Vertx vertx, VertxTestContext ctx) {
        jooqx(vertx, dsl(), initConnOptions(), initPoolOptions()).onSuccess(jooqx -> this.jooqx = jooqx)
                                                                 .onComplete(ctx.succeedingThenComplete());
        try {
            if (ctx.awaitCompletion(TIMEOUT_IN_SECOND, TimeUnit.SECONDS)) {
                ctx.failNow("Timeout when open SQL connection");
            }
        } catch (InterruptedException e) {
            ctx.failNow(e);
        }
        System.out.println(Strings.duplicate("=", 150));
    }

    @AfterEach
    public void tearDown(Vertx vertx, VertxTestContext ctx) {
        clientProvider().close(jooqx.sqlClient()).onComplete(ctx.succeedingThenComplete());
    }

    protected abstract DB getDatabase();

    /**
     * Init SQL connection options
     *
     * @return sql connection option
     */
    protected JsonObject initConnOptions() {
        final JsonObject connOptions = dbProvider().createConnOptions(getDatabase(), new JsonObject());
        connOpt = new SQLConnectionOption(connOptions);
        return connOptions;
    }

    /**
     * Init SQL pool options
     *
     * @return pool options
     * @see PoolOptions
     */
    protected @Nullable JsonObject initPoolOptions() {
        return null;
    }

    @Testcontainers
    abstract static class DBContainerSQLTest<S, B, P extends SQLPreparedQuery<B>, C extends SQLResultCollector,
                                                    E extends SQLExecutor<S, B, P, C>,
                                                    K extends JdbcDatabaseContainer<?>>
        extends SQLTestImpl<S, B, P, C, E, K, DBContainerProvider<K>> {

        @Container
        protected K db = dbProvider().init();

        @Override
        protected K getDatabase() {
            return db;
        }

    }


    abstract static class DBMemorySQLTest<S, B, P extends SQLPreparedQuery<B>, C extends SQLResultCollector,
                                             E extends SQLExecutor<S, B, P, C>>
        extends SQLTestImpl<S, B, P, C, E, String, DBMemoryProvider> implements DBMemoryProvider {

        @Override
        protected String getDatabase() {
            return dbProvider().init();
        }

        @Override
        public DBMemoryProvider dbProvider() {
            return this;
        }

    }

}
