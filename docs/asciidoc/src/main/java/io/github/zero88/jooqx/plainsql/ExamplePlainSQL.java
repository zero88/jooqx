package io.github.zero88.jooqx.plainsql;

import java.util.Objects;

import org.jooq.JSONB;
import org.jooq.impl.DSL;

import io.github.zero88.jooqx.DSLAdapter;
import io.github.zero88.jooqx.Jooqx;
import io.vertx.docgen.Source;

@Source
class ExamplePlainSQL {

    void sql(Jooqx jooqx) {
        org.jooq.meta.postgres.information_schema.tables.Tables TABLES
            = org.jooq.meta.postgres.information_schema.Tables.TABLES;
        jooqx.sql("CREATE TABLE HELLO_JOOQX (id INT)")
             .flatMap(i -> jooqx.execute(dsl -> dsl.selectFrom(TABLES).where(TABLES.TABLE_NAME.eq("HELLO_JOOQX")),
                                         DSLAdapter.fetchOne(TABLES)))
             .onSuccess(record -> {
                 assert Objects.equals(record.getValue(TABLES.TABLE_NAME), "HELLO_JOOQX");
             })
             .onFailure(Throwable::printStackTrace);
    }

    void sqlResult(Jooqx jooqx) {
        jooqx.sqlQuery("select '[\"test\"]'::jsonb", DSLAdapter.fetchOne(DSL.field("test", JSONB.class)))
             .onSuccess(rec -> {
                 final Object value = rec.getValue(0);
                 assert value instanceof org.jooq.JSONB;
                 assert Objects.equals(value, JSONB.jsonb("[\"test\"]"));
             })
             .onFailure(Throwable::printStackTrace);
    }

}
