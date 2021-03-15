package io.zero88.jooqx.integtest.spi.pg;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.Checkpoint;
import io.vertx.junit5.VertxTestContext;
import io.vertx.sqlclient.Tuple;
import io.zero88.jooqx.LegacyDSL;
import io.zero88.jooqx.UseJdbcErrorConverter;
import io.zero88.jooqx.datatype.DataTypeMapperRegistry;
import io.zero88.jooqx.datatype.UserTypeAsJooqType;
import io.zero88.jooqx.datatype.basic.JsonArrayJSONBConverter;
import io.zero88.jooqx.datatype.basic.JsonArrayJSONConverter;
import io.zero88.jooqx.integtest.pgsql.tables.JsonDataType;
import io.zero88.jooqx.integtest.pgsql.tables.JsonbDataType;
import io.zero88.jooqx.integtest.pgsql.tables.records.JsonDataTypeRecord;
import io.zero88.jooqx.integtest.pgsql.tables.records.JsonbDataTypeRecord;
import io.zero88.jooqx.integtest.spi.pg.PostgreSQLHelper.PgUseJooqType;
import io.zero88.jooqx.spi.pg.PgSQLLegacyTest;

//FIXME: Vert.x unreliable
@Disabled
class PgLeGJsonTest extends PgSQLLegacyTest implements PostgreSQLHelper, UseJdbcErrorConverter, PgUseJooqType {

    @Override
    @BeforeEach
    public void tearUp(Vertx vertx, VertxTestContext ctx) {
        super.tearUp(vertx, ctx);
        this.prepareDatabase(ctx, this, connOpt, "pg_data/json.sql");
    }

    @Override
    public DataTypeMapperRegistry typeMapperRegistry() {
        return PgUseJooqType.super.typeMapperRegistry()
                                  .addByColumn(catalog().PUBLIC.JSON_DATA_TYPE.JSONARRAY,
                                               UserTypeAsJooqType.create(new JsonArrayJSONConverter()))
                                  .addByColumn(catalog().PUBLIC.JSONB_DATA_TYPE.JSONARRAY,
                                               UserTypeAsJooqType.create(new JsonArrayJSONBConverter()));
    }

    @Test
    void queryJson(VertxTestContext ctx) {
        Checkpoint cp = ctx.checkpoint();
        final JsonDataType table = catalog().PUBLIC.JSON_DATA_TYPE;
        jooqx.execute(jooqx.dsl().selectFrom(table).limit(1), LegacyDSL.adapter().fetchOne(table),
                      ar -> ctx.verify(() -> {
                          final JsonDataTypeRecord record = assertSuccess(ctx, ar);
                          System.out.println(record);

                          Assertions.assertNull(record.getNull());
                          JsonObject data = new JsonObject("{\"str\":\"blah\", \"int\" : 1, \"float\" : 3.5, " +
                                                           "\"object\": {}, \"array\" : []   }");
                          Assertions.assertEquals(data.encode(), record.getJsonobject().data());
                          Assertions.assertEquals(new JsonArray("[1,true,null,9.5,\"Hi\"]").encode(),
                                                  record.getJsonarray().data());

                          Assertions.assertEquals(4, table.NUMBER.coerce(Integer.class).get(record));
                          Assertions.assertEquals("Hello World", table.STRING.coerce(String.class).get(record));
                          Assertions.assertEquals(true, table.BOOLEANTRUE.coerce(Boolean.class).get(record));
                          Assertions.assertEquals(false, table.BOOLEANFALSE.coerce(Boolean.class).get(record));
                          Assertions.assertEquals(Tuple.JSON_NULL, table.NULLVALUE.coerce(Object.class).get(record));

                          cp.flag();
                      }));
    }

    @Test
    void queryJsonb(VertxTestContext ctx) {
        Checkpoint cp = ctx.checkpoint();
        final JsonbDataType table = catalog().PUBLIC.JSONB_DATA_TYPE;
        jooqx.execute(jooqx.dsl().selectFrom(table).limit(1), LegacyDSL.adapter().fetchOne(table),
                      ar -> ctx.verify(() -> {
                          final JsonbDataTypeRecord record = assertSuccess(ctx, ar);
                          System.out.println(record);

                          Assertions.assertNotNull(record.getJsonobject());
                          Assertions.assertNotNull(record.getJsonarray());
                          Assertions.assertNull(record.getNull());

                          Assertions.assertEquals(4, table.NUMBER.coerce(Integer.class).get(record));
                          Assertions.assertEquals("Hello World", table.STRING.coerce(String.class).get(record));
                          Assertions.assertEquals(true, table.BOOLEANTRUE.coerce(Boolean.class).get(record));
                          Assertions.assertEquals(false, table.BOOLEANFALSE.coerce(Boolean.class).get(record));
                          Assertions.assertEquals(Tuple.JSON_NULL, table.NULLVALUE.coerce(Object.class).get(record));
                          cp.flag();
                      }));
    }

}
