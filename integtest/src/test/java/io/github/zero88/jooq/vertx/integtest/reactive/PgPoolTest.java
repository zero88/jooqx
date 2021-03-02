package io.github.zero88.jooq.vertx.integtest.reactive;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.github.zero88.jooq.vertx.BaseVertxReactiveSql;
import io.github.zero88.jooq.vertx.PostgreSQLTest.PostgreSQLReactiveTest;
import io.github.zero88.jooq.vertx.integtest.PostgreSQLHelper;
import io.github.zero88.jooq.vertx.integtest.pgsql.DefaultCatalog;
import io.github.zero88.utils.Strings;
import io.vertx.junit5.Checkpoint;
import io.vertx.junit5.VertxTestContext;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;

class PgPoolTest extends BaseVertxReactiveSql<DefaultCatalog> implements PostgreSQLReactiveTest, PostgreSQLHelper {

    @Test
    void test(VertxTestContext ctx) {
        Checkpoint flag = ctx.checkpoint();
        sqlClient().query("SELECT * FROM information_schema.tables").execute(ar -> {
            try {
                if (ar.succeeded()) {
                    RowSet<Row> result = ar.result();
                    result.iterator().forEachRemaining(row -> {
                        System.out.println(row.getValue("table_name"));
                        System.out.println(Strings.duplicate("*", 30));
                    });
                    flag.flag();
                    System.out.println("Got " + result.size() + " rows ");
                    ctx.verify(() -> Assertions.assertTrue(result.size() > 0));
                } else {
                    ctx.failNow(ar.cause());
                }
            } finally {
                flag.flag();
            }
        });
    }

    @Override
    public boolean usePool() {
        return true;
    }

}
