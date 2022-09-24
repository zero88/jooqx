package io.github.zero88.jooqx.dml;

import io.github.zero88.jooqx.DSLAdapter;
import io.github.zero88.jooqx.Jooqx;
import io.github.zero88.sample.model.pgsql.Tables;
import io.github.zero88.sample.model.pgsql.tables.Authors;
import io.vertx.docgen.Source;

@Source
class ExampleDQL {

    void fetchExists(Jooqx jooqx) {
        final Authors tbl = Tables.AUTHORS;
        jooqx.execute(dsl -> dsl.selectOne().whereExists(dsl.selectFrom(tbl).where(tbl.NAME.eq("zero88"))), // <1>
                      DSLAdapter.fetchExists())                                                             // <2>
             .onSuccess(isExisted -> System.out.println("Exists: " + isExisted))                            // <3>
             .onFailure(Throwable::printStackTrace);                                                        // <4>
    }

    void fetchCount(Jooqx jooqx) {
        final Authors tbl = Tables.AUTHORS;
        jooqx.execute(dsl -> dsl.selectCount().from(tbl).where(tbl.COUNTRY.eq("USA")),  // <1>
                      DSLAdapter.fetchCount())                                          // <2>
             .onSuccess(count -> System.out.println("Total records: " + count))         // <3>
             .onFailure(Throwable::printStackTrace);                                    // <4>
    }

    void query(Jooqx jooqx) {

    }

    void joinQuery(Jooqx jooqx) {

    }

}
