package io.zero88.jooqx.integtest.spi.pg;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.vertx.core.Vertx;
import io.vertx.junit5.Checkpoint;
import io.vertx.junit5.VertxTestContext;
import io.vertx.pgclient.PgPool;
import io.zero88.jooqx.ReactiveDSL;
import io.zero88.jooqx.integtest.pgsql.tables.NumericDataType;
import io.zero88.jooqx.integtest.pgsql.tables.records.NumericDataTypeRecord;
import io.zero88.jooqx.spi.pg.PgPoolProvider;
import io.zero88.jooqx.spi.pg.PgSQLReactiveTest;
import io.zero88.jooqx.spi.pg.UsePgSQLErrorConverter;

class PgReANumericTest extends PgSQLReactiveTest<PgPool>
    implements UsePgSQLErrorConverter, PgPoolProvider, PostgreSQLHelper {

    @Override
    @BeforeEach
    public void tearUp(Vertx vertx, VertxTestContext ctx) {
        super.tearUp(vertx, ctx);
        this.prepareDatabase(ctx, this, connOpt, "pg_data/numeric.sql");
    }

    @Test
    void queryNumeric(VertxTestContext ctx) {
        Checkpoint cp = ctx.checkpoint();
        final NumericDataType table = catalog().PUBLIC.NUMERIC_DATA_TYPE;
        jooqx.execute(jooqx.dsl().selectFrom(table).limit(1), ReactiveDSL.adapter().fetchOne(table),
                      ar -> ctx.verify(() -> {
                          final NumericDataTypeRecord record = assertSuccess(ctx, ar);
                          System.out.println(record);

                          Assertions.assertNotNull(record.getBoolean());

                          Assertions.assertNotNull(record.getInteger());
                          Assertions.assertNotNull(record.getLong());

                          Assertions.assertNotNull(record.getShort());
                          Assertions.assertNotNull(record.getSerial());
                          Assertions.assertNotNull(record.getSmallserial());
                          Assertions.assertNotNull(record.getBigserial());

                          Assertions.assertNotNull(record.getFloat());
                          Assertions.assertNotNull(record.getBigdecimal());
                          Assertions.assertNotNull(record.getDouble());
                          cp.flag();
                      }));
    }

}
