package io.github.zero88.integtest.jooqx.pg.jooq;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.github.zero88.integtest.jooqx.pg.PostgreSQLHelper.PgUseJooqType;
import io.github.zero88.jooqx.DSLAdapter;
import io.github.zero88.jooqx.datatype.DataTypeMapperRegistry;
import io.github.zero88.jooqx.datatype.UserTypeAsJooqType;
import io.github.zero88.jooqx.datatype.basic.JsonArrayJSONBConverter;
import io.github.zero88.jooqx.datatype.basic.JsonArrayJSONConverter;
import io.github.zero88.jooqx.spi.pg.PgPoolProvider;
import io.github.zero88.jooqx.spi.pg.PgSQLErrorConverterProvider;
import io.github.zero88.jooqx.spi.pg.PgSQLJooqxTest;
import io.github.zero88.sample.model.pgsql.tables.AllDataTypes;
import io.github.zero88.sample.model.pgsql.tables.records.AllDataTypesRecord;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.Checkpoint;
import io.vertx.junit5.VertxTestContext;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.Tuple;

class PgReAJsonTest extends PgSQLJooqxTest<PgPool>
    implements PgSQLErrorConverterProvider, PgPoolProvider, PgUseJooqType {

    @Override
    @BeforeEach
    public void tearUp(Vertx vertx, VertxTestContext ctx) {
        super.tearUp(vertx, ctx);
        this.prepareDatabase(ctx, this, connOpt, "pg_data/json.sql");
    }

    @Override
    public DataTypeMapperRegistry typeMapperRegistry() {
        return PgUseJooqType.super.typeMapperRegistry()
                                  .addByColumn(schema().ALL_DATA_TYPES.F_JSON_ARRAY,
                                               UserTypeAsJooqType.create(new JsonArrayJSONConverter()))
                                  .addByColumn(schema().ALL_DATA_TYPES.F_JSONB_ARRAY,
                                               UserTypeAsJooqType.create(new JsonArrayJSONBConverter()));
    }

    @Test
    void queryJson(VertxTestContext ctx) {
        Checkpoint cp = ctx.checkpoint();
        final AllDataTypes table = schema().ALL_DATA_TYPES;
        jooqx.execute(dsl -> dsl.selectFrom(table).where(table.ID.eq(51)).limit(1), DSLAdapter.fetchOne(table),
                      ar -> ctx.verify(() -> {
                          final AllDataTypesRecord record = assertSuccess(ctx, ar);
                          System.out.println(record);
                          JsonObject data = new JsonObject("{\"str\":\"blah\", \"int\" : 1, \"float\" : 3.5, " +
                                                           "\"object\": {}, \"array\" : []   }");
                          Assertions.assertEquals(data.encode(), record.getFJsonObject().data());
                          Assertions.assertEquals(new JsonArray("[1,true,null,9.5,\"Hi\"]").encode(),
                                                  record.getFJsonArray().data());

                          Assertions.assertEquals(4, table.F_JSON_NUMBER.coerce(Integer.class).get(record));
                          Assertions.assertEquals("Hello World", table.F_JSON_STRING.coerce(String.class).get(record));
                          Assertions.assertEquals(true, table.F_JSON_BOOLEAN_TRUE.coerce(Boolean.class).get(record));
                          Assertions.assertEquals(false, table.F_JSON_BOOLEAN_FALSE.coerce(Boolean.class).get(record));
                          Assertions.assertEquals(Tuple.JSON_NULL,
                                                  table.F_JSON_NULL_VALUE.coerce(Object.class).get(record));
                          Assertions.assertNull(record.getFJsonNull());

                          cp.flag();
                      }));
    }

    @Test
    void queryJsonb(VertxTestContext ctx) {
        Checkpoint cp = ctx.checkpoint();
        final AllDataTypes table = schema().ALL_DATA_TYPES;
        jooqx.execute(dsl -> dsl.selectFrom(table).where(table.ID.eq(61)).limit(1), DSLAdapter.fetchOne(table),
                      ar -> ctx.verify(() -> {
                          final AllDataTypesRecord record = assertSuccess(ctx, ar);
                          System.out.println(record);

                          Assertions.assertNotNull(record.getFJsonbObject());
                          Assertions.assertNotNull(record.getFJsonbArray());
                          Assertions.assertNull(record.getFJsonbNull());

                          Assertions.assertEquals(4, table.F_JSONB_NUMBER.coerce(Integer.class).get(record));
                          Assertions.assertEquals("Hello World", table.F_JSONB_STRING.coerce(String.class).get(record));
                          Assertions.assertEquals(true, table.F_JSONB_BOOLEAN_TRUE.coerce(Boolean.class).get(record));
                          Assertions.assertEquals(false, table.F_JSONB_BOOLEAN_FALSE.coerce(Boolean.class).get(record));
                          Assertions.assertEquals(Tuple.JSON_NULL,
                                                  table.F_JSONB_NULL_VALUE.coerce(Object.class).get(record));
                          cp.flag();
                      }));
    }

}
