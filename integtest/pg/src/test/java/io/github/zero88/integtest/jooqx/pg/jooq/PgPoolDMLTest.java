package io.github.zero88.integtest.jooqx.pg.jooq;

import java.util.Arrays;

import org.jooq.impl.DSL;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.github.zero88.integtest.jooqx.pg.PgUseJooqType;
import io.github.zero88.jooqx.DSLAdapter;
import io.github.zero88.jooqx.spi.pg.PgPoolProvider;
import io.github.zero88.jooqx.spi.pg.PgSQLErrorConverterProvider;
import io.github.zero88.jooqx.spi.pg.PgSQLJooqxTest;
import io.github.zero88.sample.model.pgsql.Tables;
import io.github.zero88.sample.model.pgsql.tables.Authors;
import io.vertx.core.Vertx;
import io.vertx.junit5.Checkpoint;
import io.vertx.junit5.VertxTestContext;
import io.vertx.pgclient.PgPool;

class PgPoolDMLTest extends PgSQLJooqxTest<PgPool>
    implements PgPoolProvider, PgUseJooqType, PgSQLErrorConverterProvider {

    @Override
    @BeforeEach
    public void tearUp(Vertx vertx, VertxTestContext ctx) {
        super.tearUp(vertx, ctx);
        this.prepareDatabase(ctx, this, connOpt, "pg_data/book_author.sql");
    }

    @Test
    void test_insert(VertxTestContext ctx) {
        final Checkpoint flag = ctx.checkpoint();
        jooqx.execute(dsl -> dsl.insertInto(Tables.BOOKS, Tables.BOOKS.ID, Tables.BOOKS.TITLE)
                                .values(Arrays.asList(DSL.defaultValue(Tables.BOOKS.ID), "Hello jOOQ.x"))
                                .returning(), DSLAdapter.fetchOne(Tables.BOOKS)).onSuccess(record -> {
            Assertions.assertEquals("Hello jOOQ.x", record.getTitle());
            Assertions.assertNotNull(record.getId());
            System.out.println(record);
            flag.flag();
        }).onFailure(ctx::failNow);
    }

    @Test
    void test_select_for_update(VertxTestContext ctx) {
        final Checkpoint flag = ctx.checkpoint();
        jooqx.fetchOne(dsl -> dsl.selectFrom(Tables.BOOKS).where(Tables.BOOKS.ID.eq(1)).forUpdate())
             .flatMap(record -> jooqx.execute(dsl -> dsl.update(Tables.BOOKS)
                                                        .set(record.setTitle("Hello jOOQ.x"))
                                                        .where(Tables.BOOKS.ID.eq(record.getId()))
                                                        .returning(), DSLAdapter.fetchOne(Tables.BOOKS)))
             .onSuccess(rec -> {
                 Assertions.assertEquals("Hello jOOQ.x", rec.getTitle());
                 Assertions.assertEquals(1, rec.getId());
                 System.out.println(rec);
                 flag.flag();
             })
             .onFailure(ctx::failNow);
    }

    @Test
    void test_update_multiple_records(VertxTestContext ctx) {
        final Checkpoint flag = ctx.checkpoint();
        final Authors tbl = Tables.AUTHORS;
        jooqx.execute(dsl -> dsl.update(tbl).set(DSL.row(tbl.COUNTRY), DSL.row("USA")).where(tbl.COUNTRY.eq("US")),
                      DSLAdapter.fetchCount())
             .flatMap(ignore -> jooqx.fetchMany(dsl -> dsl.selectFrom(tbl).where(tbl.COUNTRY.eq("USA"))))
             .onSuccess(records -> {
                 Assertions.assertEquals(6, records.size());
                 System.out.println(records);
                 flag.flag();
             })
             .onFailure(ctx::failNow);
    }

    @Test
    void test_delete(VertxTestContext ctx) {
        final Checkpoint flag = ctx.checkpoint();
        final Authors table = Tables.AUTHORS;
        jooqx.execute(dsl -> dsl.deleteFrom(table).where(table.COUNTRY.eq("US")), DSLAdapter.fetchCount())
             .flatMap(ignore -> jooqx.fetchMany(dsl -> dsl.selectFrom(table).where(table.COUNTRY.eq("US"))))
             .onSuccess(records -> {
                 Assertions.assertEquals(0, records.size());
                 System.out.println(records);
                 flag.flag();
             })
             .onFailure(ctx::failNow);
    }

}
