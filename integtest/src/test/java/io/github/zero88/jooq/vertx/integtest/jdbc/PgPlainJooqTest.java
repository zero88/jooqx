package io.github.zero88.jooq.vertx.integtest.jdbc;

import java.util.Arrays;

import org.jooq.SQLDialect;
import org.junit.jupiter.api.Test;

import io.github.zero88.jooq.vertx.BaseVertxLegacyJdbcSql;
import io.github.zero88.jooq.vertx.PostgreSQLTest.PostgreSQLJdbcTest;
import io.github.zero88.jooq.vertx.integtest.pgsql.DefaultCatalog;
import io.github.zero88.jooq.vertx.integtest.pgsql.tables.Books;
import io.github.zero88.jooq.vertx.integtest.pgsql.tables.records.BooksRecord;
import io.vertx.core.Vertx;
import io.vertx.junit5.Checkpoint;
import io.vertx.junit5.VertxTestContext;

class PgPlainJooqTest extends BaseVertxLegacyJdbcSql<DefaultCatalog> implements PostgreSQLJdbcTest {

    @Override
    public DefaultCatalog catalog() {
        return DefaultCatalog.DEFAULT_CATALOG;
    }

    @Test
    void test_insert(Vertx vertx, VertxTestContext ctx) {
        final Checkpoint flag = ctx.checkpoint();
        final Books table = catalog().PUBLIC.BOOKS;
        //        final BooksRecord rec1 = new BooksRecord().setTitle("abc");
        BooksRecord record = dsl(dataSource, SQLDialect.POSTGRES).insertInto(table, table.TITLE)
                                                                 .values(Arrays.asList("abc"))
                                                                 .returning(table.ID)
                                                                 .fetchOne();
        flag.flag();
    }

}
