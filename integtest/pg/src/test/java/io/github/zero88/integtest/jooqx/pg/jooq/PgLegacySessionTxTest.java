package io.github.zero88.integtest.jooqx.pg.jooq;

import java.util.Arrays;

import org.jooq.exception.DataAccessException;
import org.jooq.exception.SQLStateClass;
import org.jooq.impl.DSL;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.github.zero88.integtest.jooqx.pg.PgUseJooqType;
import io.github.zero88.jooqx.BindBatchValues;
import io.github.zero88.jooqx.DSLAdapter;
import io.github.zero88.jooqx.spi.jdbc.JDBCErrorConverterProvider;
import io.github.zero88.jooqx.spi.pg.PgSQLLegacyTest;
import io.github.zero88.sample.model.pgsql.tables.Authors;
import io.github.zero88.sample.model.pgsql.tables.Books;
import io.github.zero88.sample.model.pgsql.tables.records.AuthorsRecord;
import io.github.zero88.sample.model.pgsql.tables.records.BooksRecord;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.junit5.Checkpoint;
import io.vertx.junit5.VertxTestContext;

class PgLegacySessionTxTest extends PgSQLLegacyTest implements PgUseJooqType, JDBCErrorConverterProvider {

    @Override
    @BeforeEach
    public void tearUp(Vertx vertx, VertxTestContext ctx) {
        super.tearUp(vertx, ctx);
        this.prepareDatabase(ctx, this, connOpt, "pg_data/book_author.sql");
    }

    @Test
    void test_transaction_failed_when_multiple_update(VertxTestContext ctx) {
        final Checkpoint flag = ctx.checkpoint();
        final Books table = schema().BOOKS;
        final Handler<AsyncResult<BooksRecord>> asserter = ar -> {
            assertJooqException(ctx, ar, SQLStateClass.C23_INTEGRITY_CONSTRAINT_VIOLATION);
            jooqx.execute(dsl -> dsl.selectFrom(table).where(table.ID.eq(1)), DSLAdapter.fetchOne(table),
                          ar2 -> ctx.verify(() -> {
                              final BooksRecord record = assertSuccess(ctx, ar2);
                              Assertions.assertEquals("The Catcher in the Rye", record.getTitle());
                              flag.flag();
                          }));
        };
        jooqx.transaction()
             .run(tx -> tx.execute(dsl -> dsl.update(table)
                                             .set(DSL.row(table.TITLE), DSL.row("something"))
                                             .where(table.ID.eq(1))
                                             .returning(), DSLAdapter.fetchOne(table))
                          .flatMap(r -> tx.execute(dsl -> dsl.update(table)
                                                             .set(DSL.row(table.TITLE), DSL.row((String) null))
                                                             .where(table.ID.eq(2))
                                                             .returning(), DSLAdapter.fetchOne(table))), asserter);
    }

    @Test
    void test_transaction_failed_when_batch_insert(VertxTestContext ctx) {
        final Checkpoint flag = ctx.checkpoint();
        final Authors table = schema().AUTHORS;
        AuthorsRecord i1 = new AuthorsRecord().setName("n1").setCountry("AT");
        AuthorsRecord i2 = new AuthorsRecord().setName("n2");
        final BindBatchValues bindValues = new BindBatchValues().register(table.NAME, table.COUNTRY).add(i1, i2);
        jooqx.transaction()
             .run(tx -> tx.batch(tx.dsl().insertInto(table).set(bindValues.getDummyValues()), bindValues), result -> {
                 // Inconsistent msg in pg < 14 and pg >=14
                 //                 "Batch entry 1 insert into \"public\".\"authors\" (\"name\", " +
                 //                 "\"country\") values ('n2', NULL) was aborted: ERROR: null value" +
                 //                 " in column \"country\" violates not-null constraint\n" +
                 //                 "  Detail: Failing row contains (10, n2, null).  Call " +
                 //                 "getNextException to see other errors in the batch."
                 assertJooqException(ctx, result, SQLStateClass.C23_INTEGRITY_CONSTRAINT_VIOLATION);
                 jooqx.execute(jooqx.dsl().selectFrom(table), DSLAdapter.fetchMany(table), ar2 -> {
                     assertResultSize(ctx, ar2, 8);
                     flag.flag();
                 });
             });
    }

    @Test
    void test_session_throw_ex_but_still_inserted_first_when_multiple_inserts_failed_in_second(VertxTestContext ctx) {
        final Checkpoint flag = ctx.checkpoint();
        final Authors table = schema().AUTHORS;
        AuthorsRecord i1 = new AuthorsRecord().setName("n1").setCountry("AT");
        AuthorsRecord i2 = new AuthorsRecord().setName("n2");
        jooqx.session()
             .perform(s -> s.execute(dsl -> dsl.insertInto(table).set(i1).returning(), DSLAdapter.fetchOne(table))
                            .flatMap(r1 -> s.execute(dsl -> dsl.insertInto(table).set(i2).returning(),
                                                     DSLAdapter.fetchOne(table)).map(r2 -> Arrays.asList(r1, r2))))
             .onSuccess(result -> ctx.failNow("Should failed"))
             .onFailure(t -> ctx.verify(() -> {
                 Assertions.assertInstanceOf(DataAccessException.class, t);
                 Assertions.assertTrue(
                     t.getMessage().contains("null value in column \"country\" violates not-null constraint"));
                 jooqx.fetchExists(dsl -> dsl.selectFrom(table).where(table.NAME.eq("n1").and(table.COUNTRY.eq("AT"))))
                      .onSuccess(b -> ctx.verify(() -> {
                          Assertions.assertTrue(b);
                          flag.flag();
                      }))
                      .onFailure(ctx::failNow);
             }));
    }

}
