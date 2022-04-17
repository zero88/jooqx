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
import io.github.zero88.sample.model.pgsql.tables.JsonDataType;
import io.github.zero88.sample.model.pgsql.tables.JsonbDataType;
import io.github.zero88.sample.model.pgsql.tables.records.JsonDataTypeRecord;
import io.github.zero88.sample.model.pgsql.tables.records.JsonbDataTypeRecord;
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
        final JsonDataType table = schema().JSON_DATA_TYPE;
        jooqx.execute(jooqx.dsl().selectFrom(table).limit(1), DSLAdapter.fetchOne(table), ar -> ctx.verify(() -> {
            final JsonDataTypeRecord record = assertSuccess(ctx, ar);
            System.out.println(record);

            JsonObject data = new JsonObject(
                "{\"str\":\"blah\", \"int\" : 1, \"float\" : 3.5, \"object\": {}, \"array\" : []}");
            Assertions.assertEquals(data, new JsonObject(record.getJsonobject().data()));
            Assertions.assertEquals(new JsonArray("[1,true,null,9.5,\"Hi\"]"),
                                    new JsonArray(record.getJsonarray().data()));

            Assertions.assertEquals("4", record.getNumber().data());
            Assertions.assertEquals("\"Hello World\"", record.getString().data());
            Assertions.assertEquals("true", record.getBooleantrue().data());
            Assertions.assertEquals("false", record.getBooleanfalse().data());
            Assertions.assertEquals(Tuple.JSON_NULL.toString(), record.getNullvalue().data());
            Assertions.assertEquals(Tuple.JSON_NULL.toString(), record.getNull().data());

            cp.flag();
        }));
    }

    @Test
    void queryJsonb(VertxTestContext ctx) {
        Checkpoint cp = ctx.checkpoint();
        final JsonbDataType table = schema().JSONB_DATA_TYPE;
        jooqx.execute(jooqx.dsl().selectFrom(table).limit(1), DSLAdapter.fetchOne(table), ar -> ctx.verify(() -> {
            final JsonbDataTypeRecord record = assertSuccess(ctx, ar);
            System.out.println(record);

            Assertions.assertNotNull(record.getJsonobject());
            System.out.println(record.getJsonobject());
            Assertions.assertNotNull(record.getJsonarray());

            Assertions.assertEquals("4", record.getNumber().data());
            Assertions.assertEquals("\"Hello World\"", record.getString().data());
            Assertions.assertEquals("true", record.getBooleantrue().data());
            Assertions.assertEquals("false", record.getBooleanfalse().data());
            Assertions.assertEquals(Tuple.JSON_NULL.toString(), record.getNullvalue().data());
            Assertions.assertEquals(Tuple.JSON_NULL.toString(), record.getNull().data());
            cp.flag();
        }));
    }

}
