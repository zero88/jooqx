package io.github.zero88.jooqx.transaction;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

import org.jooq.InsertResultStep;
import org.jooq.exception.DataAccessException;
import org.jooq.exception.SQLStateClass;
import org.jooq.impl.DSL;

import io.github.zero88.jooqx.BlockQuery;
import io.github.zero88.jooqx.DSLAdapter;
import io.github.zero88.jooqx.Jooqx;
import io.github.zero88.jooqx.JooqxTx;
import io.github.zero88.jooqx.adapter.SelectOne;
import io.github.zero88.sample.model.pgsql.Tables;
import io.github.zero88.sample.model.pgsql.tables.Authors;
import io.github.zero88.sample.model.pgsql.tables.Books;
import io.github.zero88.sample.model.pgsql.tables.records.AuthorsRecord;
import io.github.zero88.sample.model.pgsql.tables.records.BooksRecord;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.docgen.Source;

@Source
class ExampleTransaction {

    void session(Jooqx jooqx) {
        final Authors tbl = Tables.AUTHORS;
        jooqx.session()
             .perform(session -> {
                 AuthorsRecord i1 = new AuthorsRecord().setName("n1").setCountry("AT");
                 AuthorsRecord i2 = new AuthorsRecord().setName("n2");
                 SelectOne<AuthorsRecord> selectOne = DSLAdapter.fetchOne(tbl);

                 // Avoid using the scope from outside the session:
                 // jooqx.execute(...);

                 // ...but using context within the session scope:
                 return session.execute(dsl -> dsl.insertInto(tbl).set(i1).returning(), selectOne)
                               .flatMap(r1 -> session.execute(dsl -> dsl.insertInto(tbl).set(i2).returning(), selectOne)
                                                     .map(r2 -> Arrays.asList(r1, r2)));
             })
             .onFailure(t -> {
                 assert t instanceof DataAccessException;
                 assert t.getMessage().contains("null value in column \"country\" violates not-null constraint");
             })
             .eventually(unused -> jooqx.fetchExists(dsl -> dsl.selectFrom(tbl).where(tbl.NAME.eq("n1")))
                                        .onSuccess(isExisted -> { assert isExisted; })
                                        .onFailure(Throwable::printStackTrace));
    }

    void block(Jooqx jooqx) {
        jooqx.block(dsl -> BlockQuery.create()
                                     .add(dsl.selectFrom(Tables.AUTHORS).limit(2), DSLAdapter.fetchMany(Tables.AUTHORS))
                                     .add(dsl.selectFrom(Tables.BOOKS).limit(2), DSLAdapter.fetchMany(Tables.BOOKS)))
             .onSuccess(blockResult -> {
                 assert blockResult.size() == 2;

                 final List<AuthorsRecord> authors = blockResult.get(0);
                 assert Objects.equals(authors.toString(), """
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

                 final List<BooksRecord> books = blockResult.get(1);
                 assert Objects.equals(books.toString(), """
                     [+----+-----------------------+
                     |  id|title                  |
                     +----+-----------------------+
                     |  *1|*The Catcher in the Rye|
                     +----+-----------------------+
                     , +----+-------------+
                     |  id|title        |
                     +----+-------------+
                     |  *2|*Nine Stories|
                     +----+-------------+
                     ]
                      """);
             })
             .onFailure(Throwable::printStackTrace);
    }

    void transaction(Jooqx jooqx) {
        jooqx.transaction().run(tx -> {
            InsertResultStep<BooksRecord> q1 = tx.dsl()
                                                 .insertInto(Tables.BOOKS, Tables.BOOKS.ID, Tables.BOOKS.TITLE)
                                                 .values(Arrays.asList(DSL.defaultValue(Tables.BOOKS.ID), "hello guys"))
                                                 .returning();
            InsertResultStep<BooksRecord> q2 = tx.dsl()
                                                 .insertInto(Tables.BOOKS, Tables.BOOKS.ID, Tables.BOOKS.TITLE)
                                                 .values(Arrays.asList(DSL.defaultValue(Tables.BOOKS.ID), "It's jooqx"))
                                                 .returning();
            SelectOne<BooksRecord> selectOne = DSLAdapter.fetchOne(Tables.BOOKS);
            // Avoid using the scope from outside the transaction:
            // jooqx.execute(...);

            // ...but using context within the transaction scope:
            return tx.execute(q1, selectOne).flatMap(r1 -> tx.execute(q2, selectOne).map(r2 -> Arrays.asList(r1, r2)));
        }).onSuccess(books -> {
            assert Objects.equals(books.toString(), """
                [+----+------------+
                |  id|title      |
                +----+-----------+
                |  *1|*hello guys|
                +----+-----------+
                , +----+-----------+
                |  id|title      |
                +----+-----------+
                |  *2|*It's jooqx|
                +----+-----------+
                ]
                 """);
        }).onFailure(Throwable::printStackTrace);
    }

    void rollbackTransaction(Jooqx jooqx) {
        jooqx.transaction().run(tx -> {
            final Books table = Tables.BOOKS;
            final SelectOne<BooksRecord> selectOne = DSLAdapter.fetchOne(table);
            return tx.execute(dsl -> dsl.update(table)
                                        .set(DSL.row(table.TITLE), DSL.row("something"))
                                        .where(table.ID.eq(1))
                                        .returning(), selectOne)                                            // <1>
                     .flatMap(r1 -> tx.execute(dsl -> dsl.update(table)
                                                         .set(DSL.row(table.TITLE), DSL.row((String) null))
                                                         .where(table.ID.eq(2))
                                                         .returning(), selectOne)                           // <2>
                                      .map(r2 -> Arrays.asList(r1, r2)));
        }).recover(cause -> {
            assert cause instanceof DataAccessException;                                                    // <3>
            assert Objects.equals(((DataAccessException) cause).sqlStateClass(),
                                  SQLStateClass.C23_INTEGRITY_CONSTRAINT_VIOLATION);
            return Future.succeededFuture(Collections.emptyList());                                         // <4>
        });
    }

}
