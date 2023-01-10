package io.github.zero88.jooqx.batch;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import io.github.zero88.jooqx.BatchResult;
import io.github.zero88.jooqx.BatchReturningResult;
import io.github.zero88.jooqx.BindBatchValues;
import io.github.zero88.jooqx.DSLAdapter;
import io.github.zero88.jooqx.Jooqx;
import io.github.zero88.jooqx.JsonRecord;
import io.github.zero88.sample.model.pgsql.Tables;
import io.github.zero88.sample.model.pgsql.tables.records.AuthorsRecord;
import io.vertx.core.json.JsonObject;
import io.vertx.docgen.Source;

@Source
class ExampleBatch {

    static BindBatchValues setupBatchValues() {
        AuthorsRecord rec1 = new AuthorsRecord().setName("zero88").setCountry("VN");
        AuthorsRecord rec2 = new AuthorsRecord().setName("jooq").setCountry("CH");
        AuthorsRecord rec3 = new AuthorsRecord().setName("vertx");
        return new BindBatchValues().register(Tables.AUTHORS.NAME)
                                    .registerValue(Tables.AUTHORS.COUNTRY, "FR")
                                    .add(rec1, rec2, rec3);
    }

    void batch(Jooqx jooqx) {
        BindBatchValues bindValues = setupBatchValues();
        // Wanna know success number
        jooqx.batch(dsl -> dsl.insertInto(Tables.AUTHORS).set(bindValues.getDummyValues()), bindValues, ar -> {
            final BatchResult result = ar.result();
            assert result.getTotal() == 3;
            assert result.getSuccesses() == 3;
        });
    }

    void batchWithReturning(Jooqx jooqx) {
        BindBatchValues bindValues = setupBatchValues();
        // Wanna get detail
        jooqx.batchResult(dsl -> dsl.insertInto(Tables.AUTHORS).set(bindValues.getDummyValues()).returning(),
                          bindValues, DSLAdapter.fetchJsonRecords(Tables.AUTHORS), ar -> {
                BatchReturningResult<JsonRecord<AuthorsRecord>> result = ar.result();
                List<JsonRecord<AuthorsRecord>> authorRecords = result.getRecords();
                assert result.getTotal() == 3;
                assert result.getSuccesses() == 3;
                assert Objects.equals(authorRecords.stream()
                                                   .map(JsonRecord::toJson)
                                                   .map(JsonObject::encode)
                                                   .collect(Collectors.joining(",\n", "[", "]")), """
                                          [{"id":1,"name":"zero88","country":"VN"},
                                           {"id":2,"name":"jooq","country":"CH"},
                                           {"id":3,"name":"vertx","country":"FR"}]
                                          """);
            });
    }

}
