package io.github.zero88.jooqx.transaction;

import java.util.Arrays;

import org.jooq.InsertResultStep;
import org.jooq.impl.DSL;

import io.github.zero88.jooqx.DSLAdapter;
import io.github.zero88.jooqx.Jooqx;
import io.vertx.docgen.Source;
import io.zero88.sample.data.pgsql.Tables;
import io.zero88.sample.data.pgsql.tables.records.BooksRecord;

@Source
class ExampleTransaction {

    void transaction(Jooqx jooqx) {
        jooqx.transaction().run(tx -> {
            InsertResultStep<BooksRecord> q1 = tx.dsl()
                                                 .insertInto(Tables.BOOKS, Tables.BOOKS.ID, Tables.BOOKS.TITLE)
                                                 .values(Arrays.asList(DSL.defaultValue(Tables.BOOKS.ID), "abc"))
                                                 .returning(Tables.BOOKS.ID);
            InsertResultStep<BooksRecord> q2 = tx.dsl()
                                                 .insertInto(Tables.BOOKS, Tables.BOOKS.ID, Tables.BOOKS.TITLE)
                                                 .values(Arrays.asList(DSL.defaultValue(Tables.BOOKS.ID), "xyz"))
                                                 .returning(Tables.BOOKS.ID);
            // Avoid using the scope from outside the transaction:
            // jooqx.execute(...);

            // ...but using context within the transaction scope:
            return tx.execute(q1, DSLAdapter.fetchOne(Tables.BOOKS))
                     .flatMap(b1 -> tx.execute(q2, DSLAdapter.fetchOne(Tables.BOOKS)));
        }, ar -> { });
    }

}
