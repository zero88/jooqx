package io.github.zero88.jooq.vertx;

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

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import lombok.Getter;

@Testcontainers
@ExtendWith(VertxExtension.class)
public abstract class BaseSql<S, R, E extends VertxJooqExecutor<S, R>>
    implements ConnectionProvider<S>, JooqExecutorCreation<S, R, E> {

    @Container
    protected final JdbcDatabaseContainer<?> server = createDBServer();
    @Getter
    private S client;
    @Getter
    private S pool;

    @BeforeAll
    public static void setup() {
        System.setProperty("vertx.logger-delegate-factory-class-name", "io.vertx.core.logging.SLF4JLogDelegateFactory");
        ((Logger) LoggerFactory.getLogger("ROOT")).setLevel(Level.INFO);
        ((Logger) LoggerFactory.getLogger("io.github.zero88")).setLevel(Level.DEBUG);
        ((Logger) LoggerFactory.getLogger("io.vertx.sqlclient")).setLevel(Level.DEBUG);
        ((Logger) LoggerFactory.getLogger("org.jooq")).setLevel(Level.DEBUG);
    }

    protected JdbcDatabaseContainer<?> createDBServer() {
        return get().get();
    }

    @BeforeEach
    public void tearUp(Vertx vertx, VertxTestContext ctx) {
        if (usePool()) {
            pool = createPool(vertx, ctx, server);
        } else {
            client = createConnection(vertx, ctx, server);
        }
        System.out.println(Strings.duplicate("=", 150));
    }

    @AfterEach
    public abstract void tearDown(Vertx vertx, VertxTestContext ctx);

    public S sqlClient() {
        return usePool() ? pool : client;
    }

}
