package io.github.zero88.jooqx.ddl;

import io.github.zero88.jooqx.Jooqx;
import io.vertx.docgen.Source;

@Source
class ExampleDDL {

    void ddl(Jooqx jooqx) {
        jooqx.ddl(dsl -> dsl.createSchemaIfNotExists("hello"))  // <1>
             .onSuccess(rowCount -> { assert 0 == rowCount; })  // <2>
             .onFailure(Throwable::printStackTrace);            // <3>
    }

}

