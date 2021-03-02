package io.github.zero88.jooq.vertx;

import org.junit.jupiter.api.AfterEach;

import io.vertx.core.Vertx;
import io.vertx.junit5.VertxTestContext;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import io.vertx.sqlclient.SqlClient;

public abstract class BaseVertxReactiveSql extends BaseSql<SqlClient, RowSet<Row>, VertxReactiveSqlExecutor> {

    @AfterEach
    public void tearDown(Vertx vertx, VertxTestContext ctx) {
        if (getClient() != null) {
            getClient().close(ctx.succeedingThenComplete());
        }
        if (getPool() != null) {
            getPool().close(ctx.succeedingThenComplete());
        }
    }

    @Override
    public VertxReactiveSqlExecutor createExecutor(Vertx vertx, JooqSql<?> jooq) {
        return VertxReactiveSqlExecutor.builder()
                                       .vertx(vertx)
                                       .dsl(jooq.dsl(jooq.dialect()))
                                       .sqlClient(sqlClient())
                                       .build();
    }

}
