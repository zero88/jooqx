package io.github.zero88.jooqx;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.SelectConditionStep;
import org.jooq.impl.DSL;

import io.vertx.core.Vertx;
import io.vertx.docgen.Source;
import io.vertx.pgclient.PgConnectOptions;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.PoolOptions;
import io.zero88.sample.data.pgsql.Tables;
import io.zero88.sample.data.pgsql.tables.records.AuthorsRecord;

@Source
public class JooqxBasic implements ExampleBasic {

    // @formatter:off
    public void future(Vertx vertx) {
        PgConnectOptions connectOptions = new PgConnectOptions()                // <1>
            .setPort(5432).setHost("the-host")
            .setDatabase("the-db").setUser("user").setPassword("secret");
        PoolOptions poolOptions = new PoolOptions().setMaxSize(5);              // <2>
        PgPool pgPool = PgPool.pool(vertx, connectOptions, poolOptions);        // <3>
        DSLContext dsl = DSL.using(SQLDialect.POSTGRES);                        // <4>
        Jooqx jooqx = Jooqx.builder()                                           // <5>
                           .setVertx(vertx).setDSL(dsl)
                           .setSqlClient(pgPool).build();
        SelectConditionStep<AuthorsRecord> q = dsl.selectFrom(Tables.AUTHORS)   // <6>
            .where(Tables.AUTHORS.NAME.eq("zero88"));
        jooqx.execute(q, DSLAdapter.fetchOne(q.asTable()))                      // <7>
             .onSuccess(result -> {                                             // <8>
                 AuthorsRecord rec = result;                                    // <9>
                 System.out.println(rec.getName());
                 System.out.println(rec.getCountry());
             }).onFailure(System.err::println);                                 // <10>
    }

    // @formatter:on
}
