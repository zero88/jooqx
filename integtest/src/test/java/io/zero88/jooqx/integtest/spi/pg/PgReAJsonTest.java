package io.zero88.jooqx.integtest.spi.pg;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.vertx.core.Vertx;
import io.vertx.junit5.Checkpoint;
import io.vertx.junit5.VertxTestContext;
import io.vertx.pgclient.PgPool;
import io.zero88.jooqx.ReactiveDSL;
import io.zero88.jooqx.integtest.pgsql.tables.JsonDataType;
import io.zero88.jooqx.integtest.pgsql.tables.JsonbDataType;
import io.zero88.jooqx.integtest.pgsql.tables.records.JsonDataTypeRecord;
import io.zero88.jooqx.integtest.pgsql.tables.records.JsonbDataTypeRecord;
import io.zero88.jooqx.spi.pg.PgPoolProvider;
import io.zero88.jooqx.spi.pg.PgSQLReactiveTest;
import io.zero88.jooqx.spi.pg.UsePgSQLErrorConverter;

class PgReAJsonTest extends PgSQLReactiveTest<PgPool>
    implements UsePgSQLErrorConverter, PgPoolProvider, PostgreSQLHelper {

    @Override
    @BeforeEach
    public void tearUp(Vertx vertx, VertxTestContext ctx) {
        super.tearUp(vertx, ctx);
        this.prepareDatabase(ctx, this, connOpt, "pg_data/json.sql");
    }

    @Test
    void queryJson(VertxTestContext ctx) {
        Checkpoint cp = ctx.checkpoint();
        final JsonDataType table = catalog().PUBLIC.JSON_DATA_TYPE;
        jooqx.execute(jooqx.dsl().selectFrom(table).limit(1), ReactiveDSL.adapter().fetchOne(table),
                      ar -> ctx.verify(() -> {
                          final JsonDataTypeRecord record = assertSuccess(ctx, ar);
                          System.out.println(record);

                          Assertions.assertNotNull(record.getJsonobject());
                          Assertions.assertNotNull(record.getJsonarray());

                          Assertions.assertNotNull(record.getBooleanfalse());
                          Assertions.assertNotNull(record.getBooleantrue());
                          Assertions.assertNotNull(record.getNull());
                          Assertions.assertNotNull(record.getNullvalue());

                          Assertions.assertNotNull(record.getNumber());
                          Assertions.assertNotNull(record.getString());
                          cp.flag();
                      }));
    }

    @Test
    void queryJsonb(VertxTestContext ctx) {
        Checkpoint cp = ctx.checkpoint();
        final JsonbDataType table = catalog().PUBLIC.JSONB_DATA_TYPE;
        jooqx.execute(jooqx.dsl().selectFrom(table).limit(1), ReactiveDSL.adapter().fetchOne(table),
                      ar -> ctx.verify(() -> {
                          final JsonbDataTypeRecord record = assertSuccess(ctx, ar);
                          System.out.println(record);

                          Assertions.assertNotNull(record.getJsonobject());
                          Assertions.assertNotNull(record.getJsonarray());

                          Assertions.assertNotNull(record.getBooleanfalse());
                          Assertions.assertNotNull(record.getBooleantrue());
                          Assertions.assertNotNull(record.getNull());
                          Assertions.assertNotNull(record.getNullvalue());

                          Assertions.assertNotNull(record.getNumber());
                          Assertions.assertNotNull(record.getString());
                          cp.flag();
                      }));
    }

}
