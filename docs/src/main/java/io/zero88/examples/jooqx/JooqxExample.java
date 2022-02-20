package io.zero88.examples.jooqx;

import java.util.stream.Collectors;

import org.jooq.DSLContext;
import org.jooq.InsertResultStep;
import org.jooq.Record1;
import org.jooq.SQLDialect;
import org.jooq.SelectConditionStep;
import org.jooq.SelectForUpdateStep;
import org.jooq.impl.DSL;

import io.vertx.core.Vertx;
import io.vertx.docgen.Source;
import io.vertx.jdbcclient.JDBCConnectOptions;
import io.vertx.jdbcclient.JDBCPool;
import io.vertx.mysqlclient.MySQLConnectOptions;
import io.vertx.mysqlclient.MySQLPool;
import io.vertx.pgclient.PgConnectOptions;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.PoolOptions;
import io.zero88.jooqx.BindBatchValues;
import io.zero88.jooqx.DSLAdapter;
import io.zero88.jooqx.Jooqx;
import io.zero88.jooqx.JsonRecord;
import io.zero88.sample.data.pgsql.Tables;
import io.zero88.sample.data.pgsql.tables.records.AuthorsRecord;
import io.zero88.sample.data.pgsql.tables.records.BooksRecord;

@Source
public class JooqxExample implements IExample {

    public void future(Vertx vertx) {
        // Pg connection
        PgConnectOptions connectOptions = new PgConnectOptions().setPort(5432)
                                                                .setHost("the-host")
                                                                .setDatabase("the-db")
                                                                .setUser("user")
                                                                .setPassword("secret");
        // Pool Options
        PoolOptions poolOptions = new PoolOptions().setMaxSize(5);
        // Create the pool from the data object
        PgPool pgPool = PgPool.pool(vertx, connectOptions, poolOptions);
        // jOOQ DSL
        DSLContext dsl = DSL.using(SQLDialect.POSTGRES);
        // Build jOOQ query
        Jooqx jooqx = Jooqx.builder().vertx(vertx).dsl(dsl).sqlClient(pgPool).build();
        // Build jOOQx
        SelectConditionStep<AuthorsRecord> q = dsl.selectFrom(Tables.AUTHORS).where(Tables.AUTHORS.NAME.eq("zero88"));
        // Execute
        jooqx.execute(q, DSLAdapter.fetchOne(q.asTable()), ar -> {
            if (ar.succeeded()) {
                AuthorsRecord rec = ar.result();
                System.out.println(rec.getName());
                System.out.println(rec.getCountry());
            }
        });
    }

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
        Jooqx jooqx = Jooqx.builder().vertx(vertx).dsl(dsl).sqlClient(jdbcPool).build();
        // To rx-ify version
        io.zero88.jooqx.reactivex.Jooqx.newInstance(jooqx)
                                       .rxExecute(q, DSLAdapter.fetchMany(q.asTable()))
                                       .subscribe(records -> {
                                           BooksRecord record = records.get(0);
                                           System.out.println(record.getId()); // output: 1
                                           System.out.println(record.getTitle()); // output: jooqx
                                       }, err -> {});
    }

    /**
     * Vertx JsonObject vs jOOQ Record... Ya, merging: JsonRecord
     */
    @Override
    public void jsonRecord(Vertx vertx) {
        MySQLConnectOptions connectOptions = new MySQLConnectOptions().setPort(3306)
                                                                      .setHost("the-host")
                                                                      .setDatabase("the-db")
                                                                      .setUser("user")
                                                                      .setPassword("secret");
        MySQLPool mySQLPool = MySQLPool.pool(vertx, connectOptions, new PoolOptions().setMaxSize(5));
        // jOOQ DSL
        DSLContext dsl = DSL.using(SQLDialect.MYSQL);
        // Build jOOQ query
        SelectForUpdateStep<AuthorsRecord> q = dsl.selectFrom(Tables.AUTHORS)
                                                  .where(Tables.AUTHORS.NAME.eq("zero88"))
                                                  .limit(1)
                                                  .offset(1);
        // Build jOOQx
        Jooqx jooqx = Jooqx.builder().vertx(vertx).dsl(dsl).sqlClient(mySQLPool).build();
        jooqx.execute(q, DSLAdapter.fetchJsonRecord(q.asTable()), ar -> {
            System.out.println(ar.result().toJson());
            // output: {"id":88,"name":"zero88","country":"VN"}
        });
    }

    @Override
    public void fetchExists(Vertx vertx) {
        JDBCPool jdbcPool = JDBCPool.pool(vertx, new JDBCConnectOptions().setJdbcUrl("jdbc:h2:mem:jooqx-examples"),
                                          new PoolOptions().setMaxSize(5));
        // jOOQ DSL
        DSLContext dsl = DSL.using(SQLDialect.H2);
        // Build jOOQ query
        SelectConditionStep<Record1<Integer>> q = dsl.selectOne()
                                                     .whereExists(dsl.selectFrom(Tables.AUTHORS)
                                                                     .where(Tables.AUTHORS.NAME.eq("zero88")));
        Jooqx jooqx = Jooqx.builder().vertx(vertx).dsl(dsl).sqlClient(jdbcPool).build();
        jooqx.execute(q, DSLAdapter.fetchExists(q.asTable()), ar -> {
            final Boolean isExists = ar.result();
            System.out.println(isExists);
            // output: true
        });
    }

    @Override
    public void joinQuery(Vertx vertx) {

    }

    @Override
    public void batch(Vertx vertx) {
        JDBCPool pool = JDBCPool.pool(vertx, new JDBCConnectOptions().setJdbcUrl("jdbc:h2:mem:jooqx-examples"),
                                      new PoolOptions().setMaxSize(5));
        // Build jOOQx
        Jooqx jooqx = Jooqx.builder().vertx(vertx).dsl(DSL.using(SQLDialect.H2)).sqlClient(pool).build();
        AuthorsRecord rec1 = new AuthorsRecord().setName("zero88").setCountry("VN");
        AuthorsRecord rec2 = new AuthorsRecord().setName("jooq").setCountry("CH");
        AuthorsRecord rec3 = new AuthorsRecord().setName("vertx");
        BindBatchValues bindValues = new BindBatchValues().register(Tables.AUTHORS.NAME)
                                                          .registerValue(Tables.AUTHORS.COUNTRY, "FR")
                                                          .add(rec1, rec2, rec3);
        InsertResultStep<AuthorsRecord> insert = jooqx.dsl()
                                                      .insertInto(Tables.AUTHORS)
                                                      .set(bindValues.getDummyValues())
                                                      .returning();
        // Wanna know success number
        jooqx.batch(insert, bindValues, ar -> {
            System.out.println(ar.result().getTotal());     // 3
            System.out.println(ar.result().getSuccesses()); // 3
        });
        // Wanna get detail
        jooqx.batch(insert, bindValues, DSLAdapter.fetchJsonRecords(Tables.AUTHORS), ar -> {
            System.out.println(ar.result().getRecords().stream().map(JsonRecord::toJson).collect(Collectors.toList()));
            //[{"id":1,"name":"zero88","country":"VN"},{"id":2,"name":"jooq","country":"CH"},{"id":3,"name":"vertx",
            // "country":"FR"}]
        });
    }

    @Override
    public void procedure(Vertx vertx) {

    }

    @Override
    public void transaction(Vertx vertx) {

    }

    @Override
    public void errorHandler(Vertx vertx) {

    }

}
