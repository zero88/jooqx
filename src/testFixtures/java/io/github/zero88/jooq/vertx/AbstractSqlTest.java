package io.github.zero88.jooq.vertx;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import io.github.zero88.jooq.vertx.DBProvider.DBContainerProvider;
import io.github.zero88.jooq.vertx.DBProvider.DBMemoryProvider;
import io.github.zero88.utils.Strings;
import io.vertx.core.Vertx;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import lombok.Getter;
import lombok.experimental.Accessors;

@ExtendWith(VertxExtension.class)
public abstract class AbstractSqlTest<S, P, R, E extends VertxJooqExecutor<S, P, R>, K, D extends DBProvider<K>>
    implements BaseSqlTest<S, P, R, E, K, D> {

    @Getter
    @Accessors(fluent = true)
    private S sqlClient;
    protected E executor;
    protected SqlConnectionOption connOpt;

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
        sqlClient = clientProvider().usePool()
                    ? clientProvider().createPool(vertx, ctx, connOpt)
                    : clientProvider().createConnection(vertx, ctx, connOpt);
        executor = executorProvider().createExecutor(vertx, dslProvider(), sqlClient());
        System.out.println(Strings.duplicate("=", 150));
    }

    @AfterEach
    public void tearDown(Vertx vertx, VertxTestContext ctx) {
        clientProvider().closeClient(ctx);
    }

    protected abstract K getDB();

    @Testcontainers
    public abstract static class AbstractDBContainerTest<S, P, R, E extends VertxJooqExecutor<S, P, R>,
                                                                K extends JdbcDatabaseContainer<?>>
        extends AbstractSqlTest<S, P, R, E, K, DBContainerProvider<K>> {

        @Container
        protected K db = dbProvider().get();

        @Override
        protected K getDB() {
            return db;
        }

    }


    public abstract static class AbstractDBMemoryTest<S, P, R, E extends VertxJooqExecutor<S, P, R>>
        extends AbstractSqlTest<S, P, R, E, String, DBMemoryProvider> {

        @Override
        protected String getDB() {
            return dbProvider().get();
        }

    }

}
