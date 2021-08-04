package io.zero88.jooqx.integtest.spi.pg.jooq;

import org.jooq.DSLContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.vertx.core.Vertx;
import io.vertx.junit5.Checkpoint;
import io.vertx.junit5.VertxTestContext;
import io.zero88.jooqx.DSLAdapter;
import io.zero88.jooqx.JooqDSLProvider;
import io.zero88.jooqx.integtest.pgsql.tables.TemporalDataType;
import io.zero88.jooqx.integtest.pgsql.tables.records.TemporalDataTypeRecord;
import io.zero88.jooqx.integtest.spi.pg.PostgreSQLHelper.PgLegacyType;
import io.zero88.jooqx.spi.pg.PgSQLLegacyTest;

class PgLeGTemporalTest extends PgSQLLegacyTest implements PgLegacyType {

    @BeforeEach
    @Override
    public void tearUp(Vertx vertx, VertxTestContext ctx) {
        super.tearUp(vertx, ctx);
        this.prepareDatabase(ctx, this, connOpt, "pg_data/temporal.sql");
    }

    @Test
    void test_vertx_query(VertxTestContext ctx) {
        Checkpoint flag = ctx.checkpoint();
        final TemporalDataType table = schema().TEMPORAL_DATA_TYPE;
        jooqx.execute(jooqx.dsl().selectFrom(table).limit(1), DSLAdapter.fetchOne(table), ar -> ctx.verify(() -> {
            final TemporalDataTypeRecord record = assertSuccess(ctx, ar);
            System.out.println(record);
            Assertions.assertNotNull(record.getDate());
            Assertions.assertNotNull(record.getTime());
            Assertions.assertNotNull(record.getTimetz());
            Assertions.assertNotNull(record.getTimestamptz());
            Assertions.assertNotNull(record.getTimestamp());
            //TODO: Should use converter with String as interval
            Assertions.assertNotNull(record.getInterval());
            flag.flag();
        }));
    }

    @Test
    void test_jooq_query(VertxTestContext ctx) {
        final Checkpoint flag = ctx.checkpoint();
        final DSLContext dsl = JooqDSLProvider.create(dialect(), createDataSource(connOpt)).dsl();
        final TemporalDataTypeRecord record = dsl.selectFrom(schema().TEMPORAL_DATA_TYPE).limit(1).fetchOne();
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
