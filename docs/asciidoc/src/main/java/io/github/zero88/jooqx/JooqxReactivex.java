package io.github.zero88.jooqx;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.SelectForUpdateStep;
import org.jooq.impl.DSL;

import io.vertx.core.Vertx;
import io.vertx.docgen.Source;
import io.vertx.jdbcclient.JDBCConnectOptions;
import io.vertx.jdbcclient.JDBCPool;
import io.vertx.sqlclient.PoolOptions;
import io.zero88.sample.data.pgsql.Tables;
import io.zero88.sample.data.pgsql.tables.records.BooksRecord;

@Source
public class JooqxReactivex implements ExampleReactivex {

    /**
     * Wanna use #jooq with #reactivex on #vertx... Stay tuned, one more step
     */
    @Override
    public void rx(Vertx vertx) {
        JDBCPool jdbcPool = JDBCPool.pool(vertx, new JDBCConnectOptions().setJdbcUrl("jdbc:h2:mem:jooqx-examples"),
                                          new PoolOptions().setMaxSize(5));
        // jOOQ DSL
        DSLContext dsl = DSL.using(SQLDialect.H2);
        // Build jOOQ query
        SelectForUpdateStep<BooksRecord> q = dsl.selectFrom(Tables.BOOKS)
                                                .orderBy(Tables.BOOKS.TITLE)
                                                .limit(10)
                                                .offset(5);
        // Build jOOQx
        Jooqx jooqx = Jooqx.builder().setVertx(vertx).setDSL(dsl).setSqlClient(jdbcPool).build();
        // To rx-ify version
        io.github.zero88.jooqx.reactivex.Jooqx.newInstance(jooqx)
                                       .rxExecute(q, DSLAdapter.fetchMany(q.asTable()))
                                       .subscribe(records -> {
                                           BooksRecord record = records.get(0);
                                           System.out.println(record.getId()); // output: 1
                                           System.out.println(record.getTitle()); // output: jooqx
                                       }, err -> { });
    }

    @Override
    public void rxBuilder(io.vertx.reactivex.core.Vertx vertx) {
        final JDBCConnectOptions connOptions = new JDBCConnectOptions().setJdbcUrl("jdbc:h2:mem:jooqx-examples");
        final PoolOptions poolOptions = new PoolOptions().setMaxSize(5);
        io.vertx.reactivex.jdbcclient.JDBCPool jdbcPool = io.vertx.reactivex.jdbcclient.JDBCPool.pool(vertx,
                                                                                                      connOptions,
                                                                                                      poolOptions);
        // jOOQ DSL
        DSLContext dsl = DSL.using(SQLDialect.H2);
        // Build jOOQ query
        SelectForUpdateStep<BooksRecord> q = dsl.selectFrom(Tables.BOOKS)
                                                .orderBy(Tables.BOOKS.TITLE)
                                                .limit(10)
                                                .offset(5);

        // Build jOOQx
        io.github.zero88.jooqx.reactivex.Jooqx jooqx = io.github.zero88.jooqx.reactivex.Jooqx.builder()
                                                                               .setVertx(vertx)
                                                                               .setDSL(dsl)
                                                                               .setSqlClient(jdbcPool)
                                                                               .build();
        // To rx-ify version
        jooqx.rxExecute(q, DSLAdapter.fetchMany(q.asTable())).subscribe(records -> {
            BooksRecord record = records.get(0);
            System.out.println(record.getId()); // output: 1
            System.out.println(record.getTitle()); // output: jooqx
        }, err -> { });
    }

}
