package io.github.zero88.integtest.jooqx.pg.jooq;

import org.jooq.JSON;
import org.jooq.JSONB;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.github.zero88.integtest.jooqx.pg.PostgreSQLHelper.PgLegacyType;
import io.github.zero88.jooqx.DSLAdapter;
import io.github.zero88.jooqx.datatype.DataTypeMapperRegistry;
import io.github.zero88.jooqx.datatype.UserTypeAsJooqType;
import io.github.zero88.jooqx.spi.jdbc.JDBCErrorConverterProvider;
import io.github.zero88.jooqx.spi.pg.PgSQLLegacyTest;
import io.github.zero88.sample.model.pgsql.tables.AllDataTypes;
import io.github.zero88.sample.model.pgsql.tables.records.AllDataTypesRecord;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.Checkpoint;
import io.vertx.junit5.VertxTestContext;
import io.vertx.sqlclient.Tuple;

class PgLeGJsonTest extends PgSQLLegacyTest implements JDBCErrorConverterProvider, PgLegacyType {

    @Override
    @BeforeEach
    public void tearUp(Vertx vertx, VertxTestContext ctx) {
        super.tearUp(vertx, ctx);
        this.prepareDatabase(ctx, this, connOpt, "pg_data/json.sql");
    }

    @Override
    public DataTypeMapperRegistry typeMapperRegistry() {
        UserTypeAsJooqType<String, JSON> stringJsonConverter = UserTypeAsJooqType.create(new JDBCJsonConverter());
        UserTypeAsJooqType<String, JSONB> stringJsonbConverter = UserTypeAsJooqType.create(new JDBCJsonbConverter());
        return super.typeMapperRegistry().add(stringJsonConverter).add(stringJsonbConverter);
    }

    @Test
    void queryJson(VertxTestContext ctx) {
        Checkpoint cp = ctx.checkpoint();
        final AllDataTypes table = schema().ALL_DATA_TYPES;
        jooqx.execute(dsl -> dsl.selectFrom(table).where(table.ID.eq(51)).limit(1), DSLAdapter.fetchOne(table),
                      ar -> ctx.verify(() -> {
                          final AllDataTypesRecord record = assertSuccess(ctx, ar);
                          System.out.println(record);

                          JsonObject data = new JsonObject(
                              "{\"str\":\"blah\", \"int\" : 1, \"float\" : 3.5, \"object\": {}, \"array\" : []}");
                          Assertions.assertEquals(data, new JsonObject(record.getFJsonObject().data()));
                          Assertions.assertEquals(new JsonArray("[1,true,null,9.5,\"Hi\"]"),
                                                  new JsonArray(record.getFJsonArray().data()));

                          Assertions.assertEquals("4", record.getFJsonNumber().data());
                          Assertions.assertEquals("\"Hello World\"", record.getFJsonString().data());
                          Assertions.assertEquals("true", record.getFJsonBooleanTrue().data());
                          Assertions.assertEquals("false", record.getFJsonBooleanFalse().data());
                          Assertions.assertEquals(Tuple.JSON_NULL.toString(), record.getFJsonNullValue().data());
                          Assertions.assertEquals(Tuple.JSON_NULL.toString(), record.getFJsonNull().data());

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
                          System.out.println(record.getFJsonbObject());
                          Assertions.assertNotNull(record.getFJsonbArray());

                          Assertions.assertEquals("4", record.getFJsonbNumber().data());
                          Assertions.assertEquals("\"Hello World\"", record.getFJsonbString().data());
                          Assertions.assertEquals("true", record.getFJsonbBooleanTrue().data());
                          Assertions.assertEquals("false", record.getFJsonbBooleanFalse().data());
                          Assertions.assertEquals(Tuple.JSON_NULL.toString(), record.getFJsonbNullValue().data());
                          Assertions.assertEquals(Tuple.JSON_NULL.toString(), record.getFJsonbNull().data());
                          cp.flag();
                      }));
    }

}
