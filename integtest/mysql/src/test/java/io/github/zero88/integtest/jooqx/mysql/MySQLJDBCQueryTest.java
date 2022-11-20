package io.github.zero88.integtest.jooqx.mysql;

import java.util.Arrays;
import java.util.List;

import org.jooq.impl.DSL;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.github.zero88.jooqx.BlockQuery;
import io.github.zero88.jooqx.DSLAdapter;
import io.github.zero88.jooqx.spi.jdbc.JDBCErrorConverterProvider;
import io.github.zero88.jooqx.spi.jdbc.JDBCPoolHikariProvider;
import io.github.zero88.jooqx.spi.mysql.MySQLJooqxTest;
import io.github.zero88.sample.model.mysql.Tables;
import io.github.zero88.sample.model.mysql.tables.Authors;
import io.github.zero88.sample.model.mysql.tables.records.AuthorsRecord;
import io.github.zero88.sample.model.mysql.tables.records.BooksRecord;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.jdbcclient.JDBCPool;
import io.vertx.junit5.Checkpoint;
import io.vertx.junit5.VertxTestContext;

class MySQLJDBCQueryTest extends MySQLJooqxTest<JDBCPool>
    implements JDBCPoolHikariProvider, JDBCErrorConverterProvider, MySQLHelper {

    @Override
    @BeforeEach
    public void tearUp(Vertx vertx, VertxTestContext ctx) {
        super.tearUp(vertx, ctx);
        this.prepareDatabase(ctx, connOpt, "mysql_data/book_author.sql");
    }

    @Override
    protected JsonObject initConnOptions() {
        final JsonObject connOpts = super.initConnOptions();
        return connOpts.put("jdbcUrl", connOpts.getValue("jdbcUrl") +
                                       "?allowMultiQueries=true&generateSimpleParameterMetadata=true");
    }

    @Test
    void test_query_count(VertxTestContext ctx) {
        final Checkpoint cp = ctx.checkpoint();
        jooqx.execute(dsl -> dsl.selectCount().from(Tables.BOOKS), DSLAdapter.fetchCount())
             .onSuccess(r -> ctx.verify(() -> {
                 Assertions.assertEquals(7, r);
                 cp.flag();
             }))
             .onFailure(ctx::failNow);
    }

    @Test
    void test_insert_sth(VertxTestContext ctx) {
        final Checkpoint cp = ctx.checkpoint();
        final Authors tbl = schema().AUTHORS;
        jooqx.execute(dsl -> dsl.insertInto(tbl, tbl.ID, tbl.NAME, tbl.COUNTRY)
                                .values(Arrays.asList(DSL.defaultValue(tbl.ID), "zero88", "VN")),
                      DSLAdapter.fetchOne(tbl))
             .flatMap(r -> jooqx.execute(
                 dsl -> dsl.selectOne().whereExists(dsl.selectFrom(tbl).where(tbl.NAME.eq("zero88"))),
                 DSLAdapter.fetchExists()))
             .onSuccess(r -> ctx.verify(() -> {
                 Assertions.assertTrue(r);
                 cp.flag();
             }))
             .onFailure(ctx::failNow);
    }

    @Test
    void test_select_block(VertxTestContext ctx) {
        final Checkpoint flag = ctx.checkpoint();
        jooqx.block(dsl -> BlockQuery.create()
                                     .add(dsl.select().from(schema().AUTHORS), DSLAdapter.fetchMany(schema().AUTHORS))
                                     .add(dsl.select().from(schema().BOOKS), DSLAdapter.fetchMany(schema().BOOKS)))
             .onSuccess(r -> ctx.verify(() -> {
                 List<AuthorsRecord> authors = r.get(0);
                 List<BooksRecord> books = r.get(1);
                 Assertions.assertEquals(8, authors.size());
                 Assertions.assertEquals(7, books.size());
                 System.out.println(authors);
                 System.out.println(books);
                 flag.flag();
             }))
             .onFailure(ctx::failNow);
    }

}
