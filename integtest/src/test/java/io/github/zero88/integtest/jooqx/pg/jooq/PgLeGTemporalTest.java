package io.github.zero88.integtest.jooqx.pg.jooq;

import org.jooq.DSLContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.github.zero88.integtest.jooqx.pg.PostgreSQLHelper.PgLegacyType;
import io.github.zero88.jooqx.DSLAdapter;
import io.github.zero88.jooqx.JooqDSLProvider;
import io.github.zero88.jooqx.datatype.DataTypeMapperRegistry;
import io.github.zero88.jooqx.datatype.UserTypeAsJooqType;
import io.github.zero88.jooqx.spi.pg.PgSQLLegacyTest;
import io.vertx.core.Vertx;
import io.vertx.junit5.Checkpoint;
import io.vertx.junit5.VertxTestContext;
import io.zero88.sample.data.pgsql.tables.TemporalDataType;
import io.zero88.sample.data.pgsql.tables.records.TemporalDataTypeRecord;

class PgLeGTemporalTest extends PgSQLLegacyTest implements PgLegacyType {

    @BeforeEach
    @Override
    public void tearUp(Vertx vertx, VertxTestContext ctx) {
        super.tearUp(vertx, ctx);
        this.prepareDatabase(ctx, this, connOpt, "pg_data/temporal.sql");
    }

    @Override
    public DataTypeMapperRegistry typeMapperRegistry() {
        return super.typeMapperRegistry()
                    .addByColumn(schema().TEMPORAL_DATA_TYPE.INTERVAL,
                                 UserTypeAsJooqType.create(new JDBCIntervalConverter()));
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
            Assertions.assertNotNull(record.getInterval());
            Assertions.assertEquals(10, record.getInterval().getYears());
            Assertions.assertEquals(3, record.getInterval().getMonths());
            Assertions.assertEquals(332, record.getInterval().getDays());
            Assertions.assertEquals(20, record.getInterval().getHours());
            Assertions.assertEquals(20, record.getInterval().getMinutes());
            Assertions.assertEquals(20, record.getInterval().getSeconds());
            Assertions.assertEquals(999999, record.getInterval().getMicro());
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
            Assertions.assertEquals(10, record.getInterval().getYears());
            Assertions.assertEquals(3, record.getInterval().getMonths());
            Assertions.assertEquals(332, record.getInterval().getDays());
        });
        flag.flag();
    }

}
