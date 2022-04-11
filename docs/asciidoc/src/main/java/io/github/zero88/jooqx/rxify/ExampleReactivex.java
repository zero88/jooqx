package io.github.zero88.jooqx.rxify;

import org.jooq.DSLContext;
import org.jooq.SelectForUpdateStep;

import io.github.zero88.jooqx.DSLAdapter;
import io.github.zero88.jooqx.Jooqx;
import io.vertx.docgen.Source;
import io.vertx.jdbcclient.JDBCConnectOptions;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.jdbcclient.JDBCPool;
import io.vertx.sqlclient.PoolOptions;
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

    public void rx2Builder(Vertx vertx, DSLContext dsl) {
        JDBCConnectOptions connOpts = new io.vertx.jdbcclient.JDBCConnectOptions().setJdbcUrl("jdbc:h2:mem:jooqx");
        PoolOptions poolOptions = new io.vertx.sqlclient.PoolOptions().setMaxSize(5);
        JDBCPool jdbcPool = io.vertx.reactivex.jdbcclient.JDBCPool.pool(vertx, connOpts, poolOptions);
        // Build jOOQ query
        SelectForUpdateStep<BooksRecord> q = dsl.selectFrom(Tables.BOOKS)
                                                .orderBy(Tables.BOOKS.TITLE)
                                                .limit(10)
                                                .offset(5);

        // Create rxify jooqx by builder
        io.github.zero88.jooqx.reactivex.Jooqx jooqx = io.github.zero88.jooqx.reactivex.Jooqx.builder()
                                                                                             .setVertx(vertx)
                                                                                             .setDSL(dsl)
                                                                                             .setSqlClient(jdbcPool)
                                                                                             .build();
        // Rx-execute
        jooqx.rxExecute(q, DSLAdapter.fetchMany(q.asTable())).subscribe(records -> {
            BooksRecord record = records.get(0);
            System.out.println(record.getId()); // output: 1
            System.out.println(record.getTitle()); // output: jooqx
        }, err -> { });
    }

}
