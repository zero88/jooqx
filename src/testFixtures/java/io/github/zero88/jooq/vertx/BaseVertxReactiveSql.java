package io.github.zero88.jooq.vertx;

import org.jooq.Catalog;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import io.github.zero88.jooq.vertx.ConnectionProvider.ReactiveSqlTest;
import io.github.zero88.jooq.vertx.record.VertxJooqRecord;
import io.vertx.core.Vertx;
import io.vertx.junit5.VertxTestContext;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import io.vertx.sqlclient.SqlClient;
import io.vertx.sqlclient.Tuple;

public abstract class BaseVertxReactiveSql<T extends Catalog>
    extends BaseSql<SqlClient, Tuple, RowSet<Row>, BatchReturningResult<VertxJooqRecord<?>>, VertxReactiveSqlExecutor>
    implements JooqSql<T>, ReactiveSqlTest {

    @Override
    @BeforeEach
    public void tearUp(Vertx vertx, VertxTestContext ctx) {
        super.tearUp(vertx, ctx);
        executor = createExecutor(vertx, this);
    }

    @Override
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
