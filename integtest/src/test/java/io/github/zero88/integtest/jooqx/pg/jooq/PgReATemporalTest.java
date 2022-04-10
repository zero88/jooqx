package io.github.zero88.integtest.jooqx.pg.jooq;

import java.time.Period;

import org.jooq.types.YearToSecond;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.github.zero88.integtest.jooqx.pg.PostgreSQLHelper.PgUseJooqType;
import io.github.zero88.jooqx.DSLAdapter;
import io.github.zero88.jooqx.datatype.DataTypeMapperRegistry;
import io.github.zero88.jooqx.datatype.UserTypeAsJooqType;
import io.github.zero88.jooqx.spi.pg.PgPoolProvider;
import io.github.zero88.jooqx.spi.pg.PgSQLErrorConverterProvider;
import io.github.zero88.jooqx.spi.pg.PgSQLJooqxTest;
import io.github.zero88.jooqx.spi.pg.datatype.IntervalConverter;
import io.vertx.core.Vertx;
import io.vertx.junit5.Checkpoint;
import io.vertx.junit5.VertxTestContext;
import io.vertx.pgclient.PgPool;
import io.zero88.sample.data.pgsql.tables.TemporalDataType;
import io.zero88.sample.data.pgsql.tables.records.TemporalDataTypeRecord;

class PgReATemporalTest extends PgSQLJooqxTest<PgPool>
    implements PgSQLErrorConverterProvider, PgPoolProvider, PgUseJooqType {

    @Override
    @BeforeEach
    public void tearUp(Vertx vertx, VertxTestContext ctx) {
        super.tearUp(vertx, ctx);
        this.prepareDatabase(ctx, this, connOpt, "pg_data/temporal.sql");
    }

    @Override
    public DataTypeMapperRegistry typeMapperRegistry() {
        return PgUseJooqType.super.typeMapperRegistry()
                                  .addByColumn(schema().TEMPORAL_DATA_TYPE.INTERVAL,
                                               UserTypeAsJooqType.create(new IntervalConverter()));
    }

    @Test
    void queryTemporal(VertxTestContext ctx) {
        Checkpoint cp = ctx.checkpoint();
        final TemporalDataType table = schema().TEMPORAL_DATA_TYPE;
        jooqx.execute(jooqx.dsl().selectFrom(table).limit(1), DSLAdapter.fetchOne(table), ar -> ctx.verify(() -> {
            final TemporalDataTypeRecord record = assertSuccess(ctx, ar);
            System.out.println(record);
            Assertions.assertNotNull(record.getDate());
            Assertions.assertNotNull(record.getTime());
            Assertions.assertNotNull(record.getTimetz());
            Assertions.assertNotNull(record.getTimestamp());
            Assertions.assertNotNull(record.getTimestamptz());
            Assertions.assertNotNull(record.getInterval());
            Assertions.assertEquals(10, record.getInterval().getYears());
            Assertions.assertEquals(3, record.getInterval().getMonths());
            Assertions.assertEquals(332, record.getInterval().getDays());
            cp.flag();
        }));
    }

    @Test
    void test_insert_interval(VertxTestContext ctx) {
        Checkpoint cp = ctx.checkpoint();
        final TemporalDataType table = schema().TEMPORAL_DATA_TYPE;
        final YearToSecond value1 = YearToSecond.valueOf(Period.of(1, 2, 3));
        jooqx.execute(jooqx.dsl().insertInto(table, table.ID, table.INTERVAL).values(10, value1).returning(),
                      DSLAdapter.fetchOne(table), ar -> ctx.verify(() -> {
                final TemporalDataTypeRecord record = assertSuccess(ctx, ar);
                System.out.println(record);
                Assertions.assertEquals(value1, record.getInterval());
                cp.flag();
            }));
    }

}
