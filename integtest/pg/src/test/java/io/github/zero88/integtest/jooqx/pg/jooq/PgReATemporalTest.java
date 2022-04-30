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
import io.github.zero88.sample.model.pgsql.tables.AllDataTypes;
import io.github.zero88.sample.model.pgsql.tables.records.AllDataTypesRecord;
import io.vertx.core.Vertx;
import io.vertx.junit5.Checkpoint;
import io.vertx.junit5.VertxTestContext;
import io.vertx.pgclient.PgPool;

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
                                  .addByColumn(schema().ALL_DATA_TYPES.F_INTERVAL,
                                               UserTypeAsJooqType.create(new IntervalConverter()));
    }

    @Test
    void queryTemporal(VertxTestContext ctx) {
        Checkpoint cp = ctx.checkpoint();
        final AllDataTypes table = schema().ALL_DATA_TYPES;
        jooqx.execute(dsl -> dsl.selectFrom(table).where(table.ID.eq(31)).limit(1), DSLAdapter.fetchOne(table),
                      ar -> ctx.verify(() -> {
                          final AllDataTypesRecord record = assertSuccess(ctx, ar);
                          System.out.println(record);
                          Assertions.assertNotNull(record.getFDate());
                          Assertions.assertNotNull(record.getFTime());
                          Assertions.assertNotNull(record.getFTimetz());
                          Assertions.assertNotNull(record.getFTimestamp());
                          Assertions.assertNotNull(record.getFTimestamptz());
                          Assertions.assertNotNull(record.getFInterval());
                          Assertions.assertEquals(10, record.getFInterval().getYears());
                          Assertions.assertEquals(3, record.getFInterval().getMonths());
                          Assertions.assertEquals(332, record.getFInterval().getDays());
                          cp.flag();
                      }));
    }

    @Test
    void test_insert_interval(VertxTestContext ctx) {
        Checkpoint cp = ctx.checkpoint();
        final AllDataTypes table = schema().ALL_DATA_TYPES;
        final YearToSecond value1 = YearToSecond.valueOf(Period.of(1, 2, 3));
        jooqx.execute(jooqx.dsl().insertInto(table, table.ID, table.F_INTERVAL).values(10, value1).returning(),
                      DSLAdapter.fetchOne(table), ar -> ctx.verify(() -> {
                final AllDataTypesRecord record = assertSuccess(ctx, ar);
                System.out.println(record);
                Assertions.assertEquals(value1, record.getFInterval());
                cp.flag();
            }));
    }

}
