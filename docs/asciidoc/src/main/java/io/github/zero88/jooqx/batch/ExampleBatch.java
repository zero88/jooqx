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

    void setupBatchValues() {
        AuthorsRecord rec1 = new AuthorsRecord().setName("zero88").setCountry("VN");
        AuthorsRecord rec2 = new AuthorsRecord().setName("jooq").setCountry("CH");
        AuthorsRecord rec3 = new AuthorsRecord().setName("vertx");
        BindBatchValues bindBatchValues = new BindBatchValues().register(Tables.AUTHORS.NAME)
                                                               .registerValue(Tables.AUTHORS.COUNTRY, "FR")
                                                               .add(rec1, rec2, rec3);
        assert bindBatchValues.size() == 3;
    }

    void batch(Jooqx jooqx, BindBatchValues bindBatchValues) {
        // Wanna know success number
        jooqx.batch(dsl -> dsl.insertInto(Tables.AUTHORS).set(bindBatchValues.getDummyValues()), bindBatchValues)
             .onSuccess(result -> {
                 final BatchResult batchResult = result;
                 assert batchResult.getTotal() == 3;
                 assert batchResult.getSuccesses() == 3;
             });
    }

    void batchWithReturning(Jooqx jooqx, BindBatchValues bindBatchValues) {
        // Wanna get detail
        jooqx.batchResult(dsl -> dsl.insertInto(Tables.AUTHORS).set(bindBatchValues.getDummyValues()).returning(),
                          bindBatchValues, DSLAdapter.fetchJsonRecords(Tables.AUTHORS))
             .onSuccess(batchReturningResult -> {
                 BatchReturningResult<JsonRecord<AuthorsRecord>> result = batchReturningResult;
                 List<JsonRecord<AuthorsRecord>> authorRecords = result.getRecords();
                 assert result.getTotal() == 3;
                 assert result.getSuccesses() == 3;
                 String data = authorRecords.stream()
                                            .map(JsonRecord::toJson)
                                            .map(JsonObject::encode)
                                            .collect(Collectors.joining(",\n", "[", "]"));
                 assert Objects.equals(data, """
                     [{"id":1,"name":"zero88","country":"VN"},
                      {"id":2,"name":"jooq","country":"CH"},
                      {"id":3,"name":"vertx","country":"FR"}]
                     """);
             })
             .onFailure(Throwable::printStackTrace);
    }

}
