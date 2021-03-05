package io.github.zero88.jooq.vertx.integtest.jdbc;

import java.util.Arrays;

import org.jooq.BatchBindStep;
import org.jooq.DSLContext;
import org.jooq.Result;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.github.zero88.jooq.vertx.BaseVertxLegacyJdbcSql;
import io.github.zero88.jooq.vertx.PostgreSQLTest.PostgreSQLJdbcTest;
import io.github.zero88.jooq.vertx.integtest.PostgreSQLHelper;
import io.github.zero88.jooq.vertx.integtest.pgsql.DefaultCatalog;
import io.github.zero88.jooq.vertx.integtest.pgsql.tables.Authors;
import io.github.zero88.jooq.vertx.integtest.pgsql.tables.records.AuthorsRecord;
import io.vertx.core.Vertx;
import io.vertx.junit5.Checkpoint;
import io.vertx.junit5.VertxTestContext;

class PgPlainJooqTest extends BaseVertxLegacyJdbcSql<DefaultCatalog> implements PostgreSQLJdbcTest, PostgreSQLHelper {

    @BeforeEach
    @Override
    public void tearUp(Vertx vertx, VertxTestContext ctx) {
        super.tearUp(vertx, ctx);
        this.prepareDatabase(ctx, this, server);
    }

    @Test
    void test_batch_insert(VertxTestContext ctx) {
        final Checkpoint flag = ctx.checkpoint(2);
        final Authors table = catalog().PUBLIC.AUTHORS;
        //        final BooksRecord rec1 = new BooksRecord().setTitle("abc");
        final DSLContext dsl = dsl(dataSource, SQLDialect.POSTGRES);
        final BatchBindStep bind = dsl.batch(dsl.insertInto(table, table.NAME, table.COUNTRY, table.ID)
                                                .values(Arrays.asList(null, null, DSL.defaultValue())))
                                      .bind("Erich", "Gamma", 10)
                                      .bind("Richard", "Helm", 20)
                                      .bind("Ralph", "Johnson", 30)
                                      .bind("John", "Vlissides", 40);
        bind.execute();
        flag.flag();
        final Result<AuthorsRecord> fetch = dsl.selectFrom(table).fetch();
        fetch.forEach(System.out::println);
        flag.flag();
    }

}
