package io.github.zero88.jooqx.dml;

import java.util.Objects;

import io.github.zero88.jooqx.Jooqx;
import io.github.zero88.sample.model.pgsql.Tables;
import io.github.zero88.sample.model.pgsql.tables.Authors;
import io.vertx.docgen.Source;

@Source
class ExampleDQL {

    void fetchExists(Jooqx jooqx) {
        final Authors tbl = Tables.AUTHORS;
        jooqx.fetchExists(dsl -> dsl.selectOne().whereExists(dsl.selectFrom(tbl).where(tbl.NAME.eq("zero88")))) // <1>
             .onSuccess(isExisted -> {
                 assert Objects.equals("Existed", (isExisted ? "Existed" : "Non-existed"));
             })                                                                 // <2>
             .onFailure(Throwable::printStackTrace);                            // <3>
    }

    void fetchCount(Jooqx jooqx) {
        final Authors tbl = Tables.AUTHORS;
        jooqx.fetchCount(dsl -> dsl.selectCount().from(tbl).where(tbl.COUNTRY.eq("USA")))   // <1>
             .onSuccess(count -> {
                 assert Objects.equals("Count 10", "Count " + count);
             })                                                                 // <2>
             .onFailure(Throwable::printStackTrace);                            // <3>
    }

    void fetchMany(Jooqx jooqx) {
        jooqx.fetchMany(dsl -> dsl.selectFrom(Tables.BOOKS).where(Tables.BOOKS.TITLE.like("%Great%")))  // <1>
             .onSuccess(books -> {
                 assert Objects.equals(books.toString(), """
                     [+----+-----------------+
                     |  id|title            |
                     +----+-----------------+
                     |  *4|*The Great Gatsby|
                     +----+-----------------+
                     , +----+-------------------------------------------+
                     |  id|title                                      |
                     +----+-------------------------------------------+
                     |  *8|*Using Jooq with Vert.x is Great experience|
                     +----+-------------------------------------------+
                     ]
                     """);
             })                                                             // <2>
             .onFailure(Throwable::printStackTrace);                        // <3>
    }

    void fetchOne(Jooqx jooqx) {
        final Authors tbl = Tables.AUTHORS;
        jooqx.fetchOne(dsl -> dsl.selectOne().from(tbl).where(tbl.NAME.eq("zero88")).limit(1))  // <1>
             .onSuccess(author -> {
                 assert Objects.equals(author.toString(), """
                     +----+-------+-------+
                     |  id|name   |country|
                     +----+-------+-------+
                     |  *8|*zero88|*VN    |
                     +----+-------+-------+
                     """);
             })                                                             // <2>
             .onFailure(Throwable::printStackTrace);                        // <3>
    }

    void joinQuery(Jooqx jooqx) {
        jooqx.fetchJsonArray(
                 dsl -> dsl.select(Tables.AUTHORS.asterisk(), Tables.BOOKS_AUTHORS.BOOK_ID, Tables.BOOKS.TITLE)
                           .from(Tables.AUTHORS)
                           .join(Tables.BOOKS_AUTHORS)
                           .onKey()
                           .join(Tables.BOOKS)
                           .onKey()
                           .where(Tables.AUTHORS.NAME.eq("F. Scott. Fitzgerald")))   // <1>
             .onSuccess(records -> {
                 assert Objects.equals(records.encode(), """
                     [
                       {"id":2,"name":"F. Scott. Fitzgerald","country":"USA","book_id":4,"title":"The Great Gatsby"},
                       {"id":2,"name":"F. Scott. Fitzgerald","country":"USA","book_id":5,"title":"Tender id the Night"}
                     ]
                     """);
             })                                                             // <2>
             .onFailure(Throwable::printStackTrace);                        // <3>
    }

}
