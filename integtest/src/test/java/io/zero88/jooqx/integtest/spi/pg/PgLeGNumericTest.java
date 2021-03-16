package io.zero88.jooqx.integtest.spi.pg;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.vertx.core.Vertx;
import io.vertx.junit5.Checkpoint;
import io.vertx.junit5.VertxTestContext;
import io.zero88.jooqx.DSLAdapter;
import io.zero88.jooqx.integtest.pgsql.tables.NumericDataType;
import io.zero88.jooqx.integtest.pgsql.tables.records.NumericDataTypeRecord;
import io.zero88.jooqx.spi.pg.PgSQLLegacyTest;

class PgLeGNumericTest extends PgSQLLegacyTest implements PostgreSQLHelper {

    @BeforeEach
    @Override
    public void tearUp(Vertx vertx, VertxTestContext ctx) {
        super.tearUp(vertx, ctx);
        this.prepareDatabase(ctx, this, connOpt, "pg_data/numeric.sql");
    }

    @Test
    void queryNumeric(VertxTestContext ctx) {
        Checkpoint cp = ctx.checkpoint();
        final NumericDataType table = catalog().PUBLIC.NUMERIC_DATA_TYPE;
        jooqx.execute(jooqx.dsl().selectFrom(table).limit(1), DSLAdapter.fetchOne(table), ar -> ctx.verify(() -> {
            final NumericDataTypeRecord record = assertSuccess(ctx, ar);
            System.out.println(record);

            Assertions.assertNotNull(record.getBoolean());

            Assertions.assertNotNull(record.getInteger());
            Assertions.assertNotNull(record.getLong());

            Assertions.assertNotNull(record.getFloat());
            Assertions.assertNotNull(record.getDouble());

            Assertions.assertNotNull(record.getSerial());
            Assertions.assertNotNull(record.getBigserial());

            // FIXME: Vert.x unreliable
            //                          Assertions.assertNotNull(record.getShort());
            //                          Assertions.assertNotNull(record.getSmallserial());
            //                          Assertions.assertNotNull(record.getBigdecimal());
            cp.flag();
        }));
    }

}
