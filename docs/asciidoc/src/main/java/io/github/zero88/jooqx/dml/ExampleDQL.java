package io.github.zero88.jooqx.dml;

import java.util.function.Function;

import org.jooq.DSLContext;
import org.jooq.Query;

import io.github.zero88.jooqx.DSLAdapter;
import io.github.zero88.jooqx.Jooqx;
import io.github.zero88.sample.model.pgsql.Tables;
import io.vertx.docgen.Source;

@Source
class ExampleDQL {

    void fetchCount(Jooqx jooqx) {

    }

    // @formatter:off
    void fetchExists(Jooqx jooqx) {
        Function<DSLContext, Query> q = dsl -> dsl.selectOne()                      // <1>
              .whereExists(dsl.selectFrom(Tables.AUTHORS)
                              .where(Tables.AUTHORS.NAME.eq("zero88")));
        jooqx.execute(q, DSLAdapter.fetchExists())                                  // <2>
             .onSuccess(result -> {
                 final Boolean isExists = result;                                   // <3>
                 System.out.println(isExists);
             })
            .onFailure(Throwable::printStackTrace);                                 // <4>
    }
    // @formatter:on

    void query(Jooqx jooqx) {

    }

    void joinQuery(Jooqx jooqx) {

    }

}
