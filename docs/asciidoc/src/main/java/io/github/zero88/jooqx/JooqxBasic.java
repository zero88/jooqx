package io.github.zero88.jooqx;

import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

import io.github.zero88.sample.model.pgsql.Tables;
import io.github.zero88.sample.model.pgsql.tables.records.AuthorsRecord;
import io.vertx.core.Vertx;
import io.vertx.docgen.Source;
import io.vertx.pgclient.PgConnectOptions;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.PoolOptions;

@Source
public class JooqxBasic implements ExampleBasic {

    // @formatter:off
    public void future(Vertx vertx) {
        PgConnectOptions connectOptions = new PgConnectOptions()                // <1>
            .setPort(5432).setHost("the-host")
            .setDatabase("the-db").setUser("user").setPassword("secret");
        PoolOptions poolOptions = new PoolOptions().setMaxSize(5);              // <2>
        PgPool pgPool = PgPool.pool(vertx, connectOptions, poolOptions);        // <3>
        Jooqx jooqx = Jooqx.builder()                                           // <4>
                           .setVertx(vertx)
                           .setDSL(DSL.using(SQLDialect.POSTGRES))              // <5>
                           .setSqlClient(pgPool)
                           .build();
        jooqx.execute(dsl -> dsl.selectFrom(Tables.AUTHORS)                     // <6>
                                .where(Tables.AUTHORS.NAME.eq("zero88")),
                      DSLAdapter.fetchOne(Tables.AUTHORS))                      // <7>
             .onSuccess(result -> {                                             // <8>
                 AuthorsRecord rec = result;                                    // <9>
                 System.out.println(rec.getName());
                 System.out.println(rec.getCountry());
             }).onFailure(System.err::println);                                 // <10>
    }
    // @formatter:on
}
