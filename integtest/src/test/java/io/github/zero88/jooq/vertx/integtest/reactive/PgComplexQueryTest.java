package io.github.zero88.jooq.vertx.integtest.reactive;

import java.util.List;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.SelectConditionStep;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.github.zero88.jooq.vertx.VertxJooqRecord;
import io.github.zero88.jooq.vertx.VertxReactiveDSL;
import io.github.zero88.jooq.vertx.integtest.PostgreSQLHelper;
import io.github.zero88.jooq.vertx.integtest.pgsql.Public;
import io.github.zero88.jooq.vertx.integtest.pgsql.tables.pojos.Authors;
import io.github.zero88.jooq.vertx.integtest.pgsql.tables.pojos.Books;
import io.github.zero88.jooq.vertx.spi.PostgreSQLReactiveTest.AbstractPostgreSQLReactiveTest;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.Checkpoint;
import io.vertx.junit5.VertxTestContext;

class PgComplexQueryTest extends AbstractPostgreSQLReactiveTest implements PostgreSQLHelper {

    @Override
    @BeforeEach
    public void tearUp(Vertx vertx, VertxTestContext ctx) {
        super.tearUp(vertx, ctx);
        this.prepareDatabase(ctx, this, connOpt);
    }

    @Test
    void test_join_2_table(VertxTestContext ctx) {
        final Checkpoint flag = ctx.checkpoint(2);
        final DSLContext dsl = executor.dsl();
        final Public schema = catalog().PUBLIC;
        final SelectConditionStep<Record> query = dsl.select(schema.AUTHORS.asterisk(), schema.BOOKS_AUTHORS.BOOK_ID)
                                                     .from(schema.AUTHORS)
                                                     .join(schema.BOOKS_AUTHORS)
                                                     .onKey()
                                                     .where(schema.AUTHORS.ID.eq(2));
        executor.execute(query, VertxReactiveDSL.instance().fetchVertxRecords(query.asTable()), ar -> {
            final List<VertxJooqRecord<?>> records = assertRsSize(ctx, flag, ar, 2);
            ctx.verify(() -> {
                Assertions.assertEquals(new JsonObject(
                                            "{\"id\":2,\"name\":\"F. Scott. Fitzgerald\",\"country\":\"USA\"," +
                                            "\"book_id\":4}"),
                                        records.get(0).toJson());
                Assertions.assertEquals(new JsonObject(
                                            "{\"id\":2,\"name\":\"F. Scott. Fitzgerald\",\"country\":\"USA\"," +
                                            "\"book_id\":5}"),
                                        records.get(1).toJson());
            });
            flag.flag();
        });
    }

    @Test
    void test_join_3_table(VertxTestContext ctx) {
        final Checkpoint flag = ctx.checkpoint(2);
        final DSLContext dsl = executor.dsl();
        final Public schema = catalog().PUBLIC;
        final SelectConditionStep<Record> query = dsl.select(schema.AUTHORS.asterisk(), schema.BOOKS.ID.as("book_id"),
                                                             schema.BOOKS.TITLE.as("book_title"))
                                                     .from(schema.AUTHORS)
                                                     .join(schema.BOOKS_AUTHORS)
                                                     .on(schema.AUTHORS.ID.eq(schema.BOOKS_AUTHORS.AUTHOR_ID))
                                                     .join(schema.BOOKS)
                                                     .on(schema.BOOKS.ID.eq(schema.BOOKS_AUTHORS.BOOK_ID))
                                                     .where(schema.AUTHORS.ID.eq(1));
        executor.execute(query, VertxReactiveDSL.instance().fetchVertxRecords(query.asTable()), ar -> {
            final List<VertxJooqRecord<?>> records = assertRsSize(ctx, flag, ar, 3);
            ctx.verify(() -> {
                final VertxJooqRecord<?> record1 = records.get(0);
                Assertions.assertEquals(new JsonObject("{\"id\":1,\"name\":\"J.D. Salinger\",\"country\":\"USA\"," +
                                                       "\"book_id\":1,\"book_title\":\"The Catcher in the Rye\"}"),
                                        record1.toJson());
                Assertions.assertEquals(new JsonObject("{\"id\":1,\"name\":\"J.D. Salinger\",\"country\":\"USA\"," +
                                                       "\"book_id\":2,\"book_title\":\"Nine Stories\"}"),
                                        records.get(1).toJson());
                Assertions.assertEquals(new JsonObject("{\"id\":1,\"name\":\"J.D. Salinger\",\"country\":\"USA\"," +
                                                       "\"book_id\":3,\"book_title\":\"Franny and Zooey\"}"),
                                        records.get(2).toJson());
                final Authors author = record1.into(Authors.class);
                Assertions.assertEquals(author.getId(), 1);
                Assertions.assertEquals(author.getName(), "J.D. Salinger");
                Assertions.assertEquals(author.getCountry(), "USA");
                final Books book2 = record1.map(
                    r -> new Books().setId(r.get(record1.getTable().field("book_id"), Integer.class))
                                    .setTitle(r.get(record1.getTable().field("book_title"), String.class)));
                Assertions.assertEquals(book2.getId(), 1);
                Assertions.assertEquals(book2.getTitle(), "The Catcher in the Rye");
            });
            flag.flag();
        });
    }

}
