package io.github.zero88.integtest.jooqx.pg.jooq;

import java.util.Arrays;

import org.jooq.DSLContext;
import org.jooq.InsertResultStep;
import org.jooq.InsertSetMoreStep;
import org.jooq.Record;
import org.jooq.SelectConditionStep;
import org.jooq.exception.DataAccessException;
import org.jooq.exception.SQLStateClass;
import org.jooq.impl.DSL;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.github.zero88.integtest.jooqx.pg.PgUseJooqType;
import io.github.zero88.jooqx.BatchResult;
import io.github.zero88.jooqx.BindBatchValues;
import io.github.zero88.jooqx.DSLAdapter;
import io.github.zero88.jooqx.JsonRecord;
import io.github.zero88.jooqx.spi.pg.PgPoolProvider;
import io.github.zero88.jooqx.spi.pg.PgSQLErrorConverterProvider;
import io.github.zero88.jooqx.spi.pg.PgSQLJooqxTest;
import io.github.zero88.sample.model.pgsql.Public;
import io.github.zero88.sample.model.pgsql.tables.Authors;
import io.github.zero88.sample.model.pgsql.tables.Books;
import io.github.zero88.sample.model.pgsql.tables.records.AuthorsRecord;
import io.github.zero88.sample.model.pgsql.tables.records.BooksAuthorsRecord;
import io.github.zero88.sample.model.pgsql.tables.records.BooksRecord;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.Checkpoint;
import io.vertx.junit5.VertxTestContext;
import io.vertx.pgclient.PgPool;

class PgPoolSessionTxTest extends PgSQLJooqxTest<PgPool>
    implements PgPoolProvider, PgUseJooqType, PgSQLErrorConverterProvider {

    @Override
    @BeforeEach
    public void tearUp(Vertx vertx, VertxTestContext ctx) {
        super.tearUp(vertx, ctx);
        this.prepareDatabase(ctx, this, connOpt, "pg_data/book_author.sql");
    }

    @Test
    void test_transaction_success_when_insert_into_3_tables(VertxTestContext ctx) {
        final Checkpoint flag = ctx.checkpoint();
        final DSLContext dsl = jooqx.dsl();
        final Public schema = schema().PUBLIC;
        AuthorsRecord a1 = new AuthorsRecord().setName("Lukas").setCountry("Ger");
        BooksRecord b1 = new BooksRecord().setTitle("jOOQ doc");
        jooqx.transaction()
             .run(tx -> tx.execute(dsl.insertInto(schema.AUTHORS, schema.AUTHORS.NAME, schema.AUTHORS.COUNTRY)
                                      .values(a1.value2(), a1.value3())
                                      .returning(), DSLAdapter.fetchOne(schema.AUTHORS))
                          .flatMap(r1 -> tx.execute(
                                               dsl.insertInto(schema.BOOKS, schema.BOOKS.TITLE).values(b1.value2()).returning(),
                                               DSLAdapter.fetchOne(schema.BOOKS))
                                           .flatMap(r2 -> tx.execute(
                                               dsl.insertInto(schema.BOOKS_AUTHORS, schema.BOOKS_AUTHORS.BOOK_ID,
                                                              schema.BOOKS_AUTHORS.AUTHOR_ID)
                                                  .values(r2.getId(), r1.getId())
                                                  .returning(), DSLAdapter.fetchOne(schema.BOOKS_AUTHORS)))))
             .onComplete(ar -> ctx.verify(() -> {
                 final BooksAuthorsRecord record = assertSuccess(ctx, ar);
                 Assertions.assertNotNull(record);
                 Assertions.assertEquals(12, record.getId());
                 Assertions.assertEquals(9, record.getAuthorId());
                 Assertions.assertEquals(8, record.getBookId());
                 final SelectConditionStep<Record> query = dsl.select(schema.AUTHORS.asterisk(),
                                                                      schema.BOOKS.ID.as("book_id"),
                                                                      schema.BOOKS.TITLE.as("book_title"))
                                                              .from(schema.AUTHORS)
                                                              .join(schema.BOOKS_AUTHORS)
                                                              .on(schema.AUTHORS.ID.eq(schema.BOOKS_AUTHORS.AUTHOR_ID))
                                                              .join(schema.BOOKS)
                                                              .on(schema.BOOKS.ID.eq(schema.BOOKS_AUTHORS.BOOK_ID))
                                                              .where(schema.BOOKS_AUTHORS.ID.eq(ar.result().getId()));
                 jooqx.execute(query, DSLAdapter.fetchJsonRecord(query.asTable()), ar2 -> ctx.verify(() -> {
                     final JsonRecord<?> jsonRecord = assertSuccess(ctx, ar2);
                     Assertions.assertEquals(new JsonObject("{\"id\":9,\"name\":\"Lukas\",\"country\":\"Ger\"," +
                                                            "\"book_id\":8,\"book_title\":\"jOOQ doc\"}"),
                                             jsonRecord.toJson());
                     flag.flag();
                 }));
             }));
    }

    @Test
    void test_transaction_success_when_insert(VertxTestContext context) {
        final Checkpoint flag = context.checkpoint();
        final Books table = schema().BOOKS;
        jooqx.transaction().run(tx -> {
            final InsertResultStep<BooksRecord> q1 = tx.dsl()
                                                       .insertInto(table, table.ID, table.TITLE)
                                                       .values(Arrays.asList(DSL.defaultValue(table.ID), "abc"))
                                                       .returning(table.ID);
            final InsertResultStep<BooksRecord> q2 = tx.dsl()
                                                       .insertInto(table, table.ID, table.TITLE)
                                                       .values(Arrays.asList(DSL.defaultValue(table.ID), "xyz"))
                                                       .returning(table.ID);
            return tx.execute(q1, DSLAdapter.fetchOne(table)).flatMap(b1 -> tx.execute(q2, DSLAdapter.fetchOne(table)));
        }, ar -> context.verify(() -> {
            final BooksRecord record = assertSuccess(context, ar);
            Assertions.assertNotNull(record);
            Assertions.assertEquals(9, record.getId());
            jooqx.execute(jooqx.dsl().selectFrom(table), DSLAdapter.fetchMany(table), ar2 -> {
                assertResultSize(context, ar2, 9);
                flag.flag();
            });
        }));
    }

    @Test
    void test_transaction_failed_due_to_conflict_key(VertxTestContext context) {
        final Checkpoint flag = context.checkpoint();
        final Books table = schema().BOOKS;
        jooqx.transaction().run(tx -> {
            final InsertResultStep<BooksRecord> q1 = tx.dsl()
                                                       .insertInto(table, table.ID, table.TITLE)
                                                       .values(Arrays.asList(DSL.defaultValue(table.ID), "abc"))
                                                       .returning(table.ID);
            final InsertResultStep<BooksRecord> q2 = tx.dsl()
                                                       .insertInto(table, table.ID, table.TITLE)
                                                       .values(1, "xyz")
                                                       .returning(table.ID);
            return tx.execute(q1, DSLAdapter.fetchOne(table)).flatMap(b1 -> tx.execute(q2, DSLAdapter.fetchOne(table)));
        }, ar -> context.verify(() -> {
            assertJooqException(context, ar, SQLStateClass.C23_INTEGRITY_CONSTRAINT_VIOLATION);
            jooqx.execute(jooqx.dsl().selectFrom(table), DSLAdapter.fetchMany(table), ar2 -> {
                assertResultSize(context, ar2, 7);
                flag.flag();
            });
        }));
    }

    @Test
    void test_transaction_success_when_batch_insert(VertxTestContext context) {
        final Checkpoint flag = context.checkpoint();
        final Authors table = schema().AUTHORS;
        jooqx.transaction().run(tx -> {
            AuthorsRecord rec1 = new AuthorsRecord().setName("abc").setCountry("AU");
            AuthorsRecord rec2 = new AuthorsRecord().setName("haha");
            final BindBatchValues bindValues = new BindBatchValues().register(table.NAME)
                                                                    .registerValue(table.COUNTRY, "VN")
                                                                    .add(rec1, rec2);
            final InsertSetMoreStep<AuthorsRecord> insert = jooqx.dsl()
                                                                 .insertInto(table)
                                                                 .set(bindValues.getDummyValues());
            return tx.batch(insert, bindValues);
        }, ar -> context.verify(() -> {
            final BatchResult result = assertSuccess(context, ar);
            Assertions.assertNotNull(result);
            Assertions.assertEquals(2, result.getSuccesses());
            Assertions.assertEquals(2, result.getTotal());
            jooqx.execute(jooqx.dsl().selectFrom(table), DSLAdapter.fetchMany(table), ar2 -> {
                assertResultSize(context, ar2, 10);
                flag.flag();
            });
        }));
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
