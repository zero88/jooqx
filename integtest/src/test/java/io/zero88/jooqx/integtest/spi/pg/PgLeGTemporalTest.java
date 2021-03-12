package io.zero88.jooqx.integtest.spi.pg;

import org.jooq.DSLContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.vertx.core.Vertx;
import io.vertx.junit5.Checkpoint;
import io.vertx.junit5.VertxTestContext;
import io.zero88.jooqx.LegacyDSL;
import io.zero88.jooqx.integtest.pgsql.tables.TemporalDataType;
import io.zero88.jooqx.integtest.pgsql.tables.records.TemporalDataTypeRecord;
import io.zero88.jooqx.spi.pg.PgSQLLegacyTest;

class PgLeGTemporalTest extends PgSQLLegacyTest implements PostgreSQLHelper {

    @BeforeEach
    @Override
    public void tearUp(Vertx vertx, VertxTestContext ctx) {
        super.tearUp(vertx, ctx);
        this.prepareDatabase(ctx, this, connOpt, "pg_datatype/temporal.sql");
    }

    @Test
    void queryTemporal(VertxTestContext ctx) {
        final Checkpoint flag = ctx.checkpoint();
        final TemporalDataType table = catalog().PUBLIC.TEMPORAL_DATA_TYPE;
        jooqx.execute(jooqx.dsl().selectFrom(table).limit(1), LegacyDSL.adapter().fetchOne(table), ar -> {
            final TemporalDataTypeRecord record = ar.result();
            ctx.verify(() -> {
                System.out.println(record);
                Assertions.assertNotNull(record.getDate());
                Assertions.assertNotNull(record.getTime());
                Assertions.assertNotNull(record.getTimetz());
                Assertions.assertNotNull(record.getTimestamp());
                Assertions.assertNotNull(record.getTimestamptz());
                Assertions.assertNotNull(record.getInterval());
            });
            flag.flag();
        });
    }

    @Test
    void test_jooq_query(VertxTestContext ctx) {
        final Checkpoint flag = ctx.checkpoint(2);
        final DSLContext dsl = dsl(createDataSource(connOpt));
        final TemporalDataTypeRecord record = dsl.selectFrom(catalog().PUBLIC.TEMPORAL_DATA_TYPE).limit(1).fetchOne();
        flag.flag();
        ctx.verify(() -> {
            System.out.println(record);
            Assertions.assertNotNull(record);
            Assertions.assertNotNull(record.getDate());
            Assertions.assertNotNull(record.getTime());
            Assertions.assertNotNull(record.getTimetz());
            Assertions.assertNotNull(record.getTimestamp());
            Assertions.assertNotNull(record.getTimestamptz());
            Assertions.assertNotNull(record.getInterval());
        });
        flag.flag();
    }

}
