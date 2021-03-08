package io.github.zero88.jooq.vertx;

import javax.sql.DataSource;

import org.jooq.Catalog;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.slf4j.LoggerFactory;

import io.github.zero88.jooq.vertx.ConnectionProvider.LegacyJdbcSqlTest;
import io.github.zero88.jooq.vertx.converter.LegacyBindParamConverter;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.sql.ResultSet;
import io.vertx.ext.sql.SQLClient;
import io.vertx.junit5.VertxTestContext;

import com.zaxxer.hikari.HikariDataSource;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;

public abstract class BaseVertxLegacyJdbcSql<T extends Catalog>
    extends BaseSql<SQLClient, JsonArray, ResultSet, VertxLegacyJdbcExecutor> implements JooqSql<T>, LegacyJdbcSqlTest {

    protected HikariDataSource dataSource;

    @BeforeAll
    public static void setup() {
        BaseSql.setup();
        ((Logger) LoggerFactory.getLogger("com.zaxxer.hikari")).setLevel(Level.DEBUG);
    }

    @Override
    public DataSource getDataSource() {
        return dataSource = createDataSource(server);
    }

    @BeforeEach
    public void tearUp(Vertx vertx, VertxTestContext ctx) {
        super.tearUp(vertx, ctx);
        executor = createExecutor(vertx, this);
    }

    @AfterEach
    public void tearDown(Vertx vertx, VertxTestContext ctx) {
        closeDataSource(dataSource);
        sqlClient().close(ctx.succeedingThenComplete());
    }

    @Override
    public VertxLegacyJdbcExecutor createExecutor(Vertx vertx, JooqSql<?> jooq) {
        return VertxLegacyJdbcExecutor.builder()
                                      .vertx(vertx)
                                      .dsl(jooq.dsl(jooq.dialect()))
                                      .sqlClient(sqlClient())
                                      .helper(createQueryHelper())
                                      .errorMaker(createErrorMaker())
                                      .build();
    }

    @Override
    public QueryHelper<JsonArray> createQueryHelper() {
        return new QueryHelper<>(new LegacyBindParamConverter());
    }

}
