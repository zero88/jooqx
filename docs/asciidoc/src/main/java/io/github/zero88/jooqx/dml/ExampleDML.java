package io.github.zero88.jooqx.dml;

import java.util.Arrays;
import java.util.Objects;

import org.jooq.impl.DSL;

import io.github.zero88.jooqx.DSLAdapter;
import io.github.zero88.jooqx.Jooqx;
import io.github.zero88.sample.model.pgsql.Tables;
import io.github.zero88.sample.model.pgsql.tables.Authors;
import io.vertx.docgen.Source;

@Source
class ExampleDML {

    void insert(Jooqx jooqx) {
        jooqx.execute(dsl -> dsl.insertInto(Tables.BOOKS, Tables.BOOKS.ID, Tables.BOOKS.TITLE)
                                .values(Arrays.asList(DSL.defaultValue(Tables.BOOKS.ID), "Hello jOOQ.x"))
                                .returning(), DSLAdapter.fetchOne(Tables.BOOKS))                            // <1>
             .onSuccess(book -> {
                 assert Objects.equals(book.toString(), """
                     +----+-------------+
                     |  id|title        |
                     +----+-------------+
                     | *10|*Hello jOOQ.x|
                     +----+-------------+
                     """);
             })                                                     // <2>
             .onFailure(Throwable::printStackTrace);                // <3>
    }

    void selectForUpdate(Jooqx jooqx) {
        jooqx.fetchOne(dsl -> dsl.selectFrom(Tables.BOOKS).where(Tables.BOOKS.ID.eq(1)).forUpdate())    // <1>
             .flatMap(book -> jooqx.execute(dsl -> dsl.update(Tables.BOOKS)
                                                      .set(book.setTitle("Hello jOOQ.x"))
                                                      .where(Tables.BOOKS.ID.eq(book.getId()))
                                                      .returning(), DSLAdapter.fetchOne(Tables.BOOKS)))         // <2>
             .onSuccess(book -> {
                 assert Objects.equals(book.toString(), """
                     +----+-------------+
                     |  id|title        |
                     +----+-------------+
                     |  *1|*Hello jOOQ.x|
                     +----+-------------+
                     """);
             })                                                     // <3>
             .onFailure(Throwable::printStackTrace);                // <4>
    }

    void updateMany(Jooqx jooqx) {
        final Authors tbl = Tables.AUTHORS;
        jooqx.execute(dsl -> dsl.update(tbl).set(DSL.row(tbl.COUNTRY), DSL.row("USA")).where(tbl.COUNTRY.eq("US")),
                      DSLAdapter.fetchCount())                                                                          // <1>
             .flatMap(ignore -> jooqx.fetchMany(dsl -> dsl.selectFrom(tbl).where(tbl.COUNTRY.eq("USA"))))       // <2>
             .onSuccess(book -> {
                 assert Objects.equals(book.toString(), """
                      [+----+--------------+-------+
                      |  id|name          |country|
                      +----+--------------+-------+
                      |  *1|*J.D. Salinger|*USA   |
                      +----+--------------+-------+
                      , +----+---------------------+-------+
                      |  id|name                 |country|
                      +----+---------------------+-------+
                      |  *2|*F. Scott. Fitzgerald|*USA   |
                      +----+---------------------+-------+
                      ]
                     """);
             })                                                     // <3>
             .onFailure(Throwable::printStackTrace);                // <4>
    }

    void delete(Jooqx jooqx) {
        final Authors table = Tables.AUTHORS;
        jooqx.execute(dsl -> dsl.deleteFrom(table).where(table.COUNTRY.eq("US")), DSLAdapter.fetchCount())      // <1>
             .flatMap(ignore -> jooqx.fetchMany(dsl -> dsl.selectFrom(table).where(table.COUNTRY.eq("US"))))    // <2>
             .onSuccess(books -> {
                 assert Objects.equals(books.toString(), "[]");
             })                                                     // <3>
             .onFailure(Throwable::printStackTrace);                // <4>
    }

}
