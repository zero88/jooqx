package io.github.zero88.jooqx.rxify;

import org.jooq.DSLContext;
import org.jooq.SelectForUpdateStep;

import io.github.zero88.jooqx.DSLAdapter;
import io.github.zero88.jooqx.Jooqx;
import io.vertx.docgen.Source;
import io.zero88.sample.data.pgsql.Tables;
import io.zero88.sample.data.pgsql.tables.records.BooksRecord;

@Source
class ExampleReactivex {

    public void rx2(Jooqx jooqx) {
        // Build jOOQ query
        SelectForUpdateStep<BooksRecord> q = jooqx.dsl()
                                                  .selectFrom(Tables.BOOKS)
                                                  .orderBy(Tables.BOOKS.TITLE)
                                                  .limit(10)
                                                  .offset(5);
        // To rx-ify version
        io.github.zero88.jooqx.reactivex.Jooqx rxJooqx = io.github.zero88.jooqx.reactivex.Jooqx.newInstance(jooqx);

        // Rx-execute
        rxJooqx.rxExecute(q, DSLAdapter.fetchMany(q.asTable())).subscribe(records -> {
            BooksRecord record = records.get(0);
            System.out.println(record.getId()); // output: 1
            System.out.println(record.getTitle()); // output: jooqx
        }, err -> { });
    }

    public void rx2Builder(io.vertx.core.Vertx vertx, io.vertx.jdbcclient.JDBCPool pool, DSLContext dsl) {
        io.vertx.reactivex.core.Vertx vertxRx2 = io.vertx.reactivex.core.Vertx.newInstance(vertx);
        io.vertx.reactivex.jdbcclient.JDBCPool poolRx2 = io.vertx.reactivex.jdbcclient.JDBCPool.newInstance(pool);
        // Build jOOQ query
        SelectForUpdateStep<BooksRecord> q = dsl.selectFrom(Tables.BOOKS)
                                                .orderBy(Tables.BOOKS.TITLE)
                                                .limit(10)
                                                .offset(5);

        // Create rx2 jooqx by builder
        io.github.zero88.jooqx.reactivex.Jooqx jooqx = io.github.zero88.jooqx.reactivex.Jooqx.builder()
                                                                                             .setVertx(vertxRx2)
                                                                                             .setSqlClient(poolRx2)
                                                                                             .setDSL(dsl)
                                                                                             .build();
        // Rx-execute
        jooqx.rxExecute(q, DSLAdapter.fetchMany(q.asTable())).subscribe(records -> {
            BooksRecord record = records.get(0);
            System.out.println(record.getId()); // output: 1
            System.out.println(record.getTitle()); // output: jooqx
        }, err -> { });
    }

    public void rx3Builder(io.vertx.core.Vertx vertx, io.vertx.mysqlclient.MySQLPool pool, DSLContext dsl) {
        io.vertx.rxjava3.core.Vertx vertxRx3 = io.vertx.rxjava3.core.Vertx.newInstance(vertx);
        io.vertx.rxjava3.mysqlclient.MySQLPool poolRx3 = io.vertx.rxjava3.mysqlclient.MySQLPool.newInstance(pool);
        // Build jOOQ query
        SelectForUpdateStep<BooksRecord> q = dsl.selectFrom(Tables.BOOKS)
                                                .orderBy(Tables.BOOKS.TITLE)
                                                .limit(10)
                                                .offset(5);

        // Create rx3 jooqx by builder
        io.github.zero88.jooqx.rxjava3.Jooqx jooqx = io.github.zero88.jooqx.rxjava3.Jooqx.builder()
                                                                                         .setVertx(vertxRx3)
                                                                                         .setSqlClient(poolRx3)
                                                                                         .setDSL(dsl)
                                                                                         .build();
        // Rx-execute
        jooqx.rxExecute(q, DSLAdapter.fetchMany(q.asTable())).subscribe(records -> {
            BooksRecord record = records.get(0);
            System.out.println(record.getId()); // output: 1
            System.out.println(record.getTitle()); // output: jooqx
        }, err -> { });
    }

}
