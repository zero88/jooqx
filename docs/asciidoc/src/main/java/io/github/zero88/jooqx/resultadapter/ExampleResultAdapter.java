package io.github.zero88.jooqx.resultadapter;

import java.util.List;

import org.jooq.InsertResultStep;
import org.jooq.Record;
import org.jooq.SelectForUpdateStep;
import org.jooq.SelectWhereStep;

import io.github.zero88.jooqx.DSLAdapter;
import io.github.zero88.jooqx.Jooqx;
import io.github.zero88.jooqx.JsonRecord;
import io.github.zero88.sample.model.pgsql.Tables;
import io.github.zero88.sample.model.pgsql.tables.Books;
import io.github.zero88.sample.model.pgsql.tables.pojos.Authors;
import io.github.zero88.sample.model.pgsql.tables.records.AuthorsRecord;
import io.github.zero88.sample.model.pgsql.tables.records.BooksRecord;
import io.vertx.docgen.Source;

@Source
class ExampleResultAdapter {

    // @formatter:off
    void toJsonRecord(Jooqx jooqx) {
        /*
         * Vertx JsonObject vs jOOQ Record... Ya, merging: JsonRecord
         */
        jooqx.fetchJsonRecord(dsl -> dsl.selectFrom(Tables.AUTHORS)                         // <1>
                                        .where(Tables.AUTHORS.NAME.eq("zero88"))
                                        .limit(1)
                                        .offset(1))
             .onSuccess(r -> {
                 JsonRecord<AuthorsRecord> record = r;                                      // <2>
                 System.out.println(record.toJson());                                       // <3>
             });
    }
    // @formatter:on

    void toClass(Jooqx jooqx) {
        SelectWhereStep<AuthorsRecord> query = jooqx.dsl().selectFrom(Tables.AUTHORS);
        // Authors is POJO class that generated by jOOQ
        jooqx.execute(query, DSLAdapter.fetchMany(Tables.AUTHORS, Authors.class), ar -> {
            List<Authors> authors = ar.result();
            Authors author = authors.get(0);
            System.out.println(author.getId());                 //  output: 1
            System.out.println(author.getCountry());            //  output: UK
        });
    }

    void toFields(Jooqx jooqx) {
        Books table = Tables.BOOKS;
        InsertResultStep<BooksRecord> insert = jooqx.dsl().insertInto(table, table.TITLE).values("aha").returning();
        jooqx.execute(insert, DSLAdapter.fetchOne(table.ID), ar -> {
            Record record = ar.result();
            System.out.println(record.getValue(0));
        });
    }

}