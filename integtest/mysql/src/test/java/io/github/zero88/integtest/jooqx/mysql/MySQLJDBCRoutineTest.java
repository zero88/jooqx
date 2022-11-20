package io.github.zero88.integtest.jooqx.mysql;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.github.zero88.jooqx.DSLAdapter;
import io.github.zero88.jooqx.spi.jdbc.JDBCErrorConverterProvider;
import io.github.zero88.jooqx.spi.jdbc.JDBCPoolHikariProvider;
import io.github.zero88.jooqx.spi.mysql.MySQLJooqxTest;
import io.github.zero88.sample.model.mysql.Tables;
import io.github.zero88.sample.model.mysql.routines.CountAuthorByCountry;
import io.github.zero88.sample.model.mysql.routines.Hello;
import io.github.zero88.sample.model.mysql.routines.RemoveAuthor;
import io.github.zero88.sample.model.mysql.routines.SelectBooksByAuthor;
import io.github.zero88.sample.model.mysql.tables.Books;
import io.github.zero88.sample.model.mysql.tables.records.BooksRecord;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.jdbcclient.JDBCPool;
import io.vertx.junit5.Checkpoint;
import io.vertx.junit5.VertxTestContext;

class MySQLJDBCRoutineTest extends MySQLJooqxTest<JDBCPool>
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
        // https://dev.mysql.com/doc/connector-j/8.0/en/connector-j-connp-props-prepared-statements.html
        return connOpts.put("jdbcUrl", connOpts.getValue("jdbcUrl") +
                                       "?allowMultiQueries=true&generateSimpleParameterMetadata=true");
    }

    @Test
    void test_fn_returns_value(VertxTestContext ctx) {
        final Checkpoint cp = ctx.checkpoint();
        final Hello hello = new Hello();
        hello.setS("hehe");
        jooqx.routine(hello).onSuccess(result -> ctx.verify(() -> {
            Assertions.assertEquals("Hello, hehe!", result);
            cp.flag();
        })).onFailure(ctx::failNow);
    }

    @Test
    void test_proc_returns_void(VertxTestContext ctx) {
        final Checkpoint cp = ctx.checkpoint(2);
        final RemoveAuthor proc = new RemoveAuthor();
        final String authorName = "J.D. Salinger";
        proc.setAuthorName(authorName);
        jooqx.routine(proc)
             .flatMap(r -> jooqx.execute(dsl -> dsl.selectOne()
                                                   .whereExists(dsl.selectFrom(Tables.AUTHORS)
                                                                   .where(Tables.AUTHORS.NAME.eq(authorName))),
                                         DSLAdapter.fetchExists()))
             .onSuccess(r -> ctx.verify(() -> {
                 Assertions.assertFalse(r);
                 cp.flag();
             }))
             .flatMap(r -> jooqx.execute(dsl -> dsl.select().from(Tables.BOOKS), DSLAdapter.fetchMany(Tables.BOOKS)))
             .onSuccess(records -> ctx.verify(() -> {
                 Assertions.assertEquals(4, records.size());
                 cp.flag();
             }))
             .onFailure(ctx::failNow);
    }

    @Test
    void test_proc_returns_outParams(VertxTestContext ctx) {
        final CountAuthorByCountry byCountry = new CountAuthorByCountry();
        byCountry.setCountry("USA");
        Checkpoint cp = ctx.checkpoint();
        jooqx.routineResult(byCountry).onSuccess(routineResult -> ctx.verify(() -> {
            Assertions.assertNotNull(routineResult.getRecord());
            Assertions.assertEquals(1, routineResult.getRecord().fields().length);
            Assertions.assertEquals(6, routineResult.getRecord().get(0));
            Assertions.assertEquals(6, routineResult.getRecord().get("count"));
            Assertions.assertEquals("count", routineResult.getRecord().fields()[0].getName());
            cp.flag();
        })).onFailure(ctx::failNow);
    }

    @Test
    void test_proc_return_resultSet(VertxTestContext ctx) {
        final Checkpoint cp = ctx.checkpoint();
        final SelectBooksByAuthor proc = new SelectBooksByAuthor();
        proc.setAuthor("Jane Austen");
        jooqx.routineResultSet(proc, DSLAdapter.fetchMany(Books.BOOKS)).onSuccess(records -> ctx.verify(() -> {
            Assertions.assertEquals(1, records.size());
            BooksRecord rec = records.get(0);
            Assertions.assertEquals(6, rec.getId());
            Assertions.assertEquals("Pride and Prejudice", rec.getTitle());
            cp.flag();
        })).onFailure(ctx::failNow);
    }

}
