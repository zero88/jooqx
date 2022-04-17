package io.github.zero88.jooqx.batch;

import java.util.stream.Collectors;

import org.jooq.InsertResultStep;

import io.github.zero88.jooqx.BindBatchValues;
import io.github.zero88.jooqx.DSLAdapter;
import io.github.zero88.jooqx.Jooqx;
import io.github.zero88.jooqx.JsonRecord;
import io.github.zero88.sample.model.pgsql.Tables;
import io.github.zero88.sample.model.pgsql.tables.records.AuthorsRecord;
import io.vertx.docgen.Source;

@Source
class ExampleBatch {

    void batch(Jooqx jooqx) {
        // Build jOOQx
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
        jooqx.batchResult(insert, bindValues, DSLAdapter.fetchJsonRecords(Tables.AUTHORS), ar -> {
            System.out.println(ar.result().getRecords().stream().map(JsonRecord::toJson).collect(Collectors.toList()));
            //[{"id":1,"name":"zero88","country":"VN"},{"id":2,"name":"jooq","country":"CH"},{"id":3,"name":"vertx",
            // "country":"FR"}]
        });
    }

}
