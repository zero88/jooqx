package io.github.zero88.integtest.jooqx.pg.jooq;

import org.jooq.DSLContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.github.zero88.integtest.jooqx.pg.JDBCIntervalConverter;
import io.github.zero88.integtest.jooqx.pg.PgUseJooqType;
import io.github.zero88.jooqx.DSLAdapter;
import io.github.zero88.jooqx.JooqDSLProvider;
import io.github.zero88.jooqx.datatype.DataTypeMapperRegistry;
import io.github.zero88.jooqx.datatype.UserTypeAsJooqType;
import io.github.zero88.jooqx.spi.pg.PgSQLLegacyTest;
import io.github.zero88.sample.model.pgsql.tables.AllDataTypes;
import io.github.zero88.sample.model.pgsql.tables.records.AllDataTypesRecord;
import io.vertx.core.Vertx;
import io.vertx.junit5.Checkpoint;
import io.vertx.junit5.VertxTestContext;

class PgLeGTemporalTest extends PgSQLLegacyTest implements PgUseJooqType {

    @BeforeEach
    @Override
    public void tearUp(Vertx vertx, VertxTestContext ctx) {
        super.tearUp(vertx, ctx);
        this.prepareDatabase(ctx, this, connOpt, "pg_data/temporal.sql");
    }

    @Override
    public DataTypeMapperRegistry typeMapperRegistry() {
        return super.typeMapperRegistry()
                    .addByColumn(schema().ALL_DATA_TYPES.F_INTERVAL,
                                 UserTypeAsJooqType.create(new JDBCIntervalConverter()));
    }

    @Test
    void test_vertx_query(VertxTestContext ctx) {
        Checkpoint flag = ctx.checkpoint();
        final AllDataTypes table = schema().ALL_DATA_TYPES;
        jooqx.execute(dsl -> dsl.selectFrom(table).where(table.ID.eq(31)).limit(1), DSLAdapter.fetchOne(table),
                      ar -> ctx.verify(() -> {
                          final AllDataTypesRecord record = assertSuccess(ctx, ar);
                          System.out.println(record);
                          Assertions.assertNotNull(record.getFDate());
                          Assertions.assertNotNull(record.getFTime());
                          Assertions.assertNotNull(record.getFTimetz());
                          Assertions.assertNotNull(record.getFTimestamptz());
                          Assertions.assertNotNull(record.getFTimestamp());
                          Assertions.assertNotNull(record.getFInterval());
                          Assertions.assertEquals(10, record.getFInterval().getYears());
                          Assertions.assertEquals(3, record.getFInterval().getMonths());
                          Assertions.assertEquals(332, record.getFInterval().getDays());
                          Assertions.assertEquals(20, record.getFInterval().getHours());
                          Assertions.assertEquals(20, record.getFInterval().getMinutes());
                          Assertions.assertEquals(20, record.getFInterval().getSeconds());
                          Assertions.assertEquals(999999, record.getFInterval().getMicro());
                          flag.flag();
                      }));
    }

    @Test
    void test_jooq_query(VertxTestContext ctx) {
        final Checkpoint flag = ctx.checkpoint();
        final DSLContext dsl = JooqDSLProvider.create(dialect(), createDataSource(connOpt)).dsl();
        final AllDataTypes table = schema().ALL_DATA_TYPES;
        final AllDataTypesRecord record = dsl.selectFrom(table).where(table.ID.eq(31)).fetchOne();
        ctx.verify(() -> {
            System.out.println(record);
            Assertions.assertNotNull(record);
            Assertions.assertNotNull(record.getFDate());
            Assertions.assertNotNull(record.getFTime());
            Assertions.assertNotNull(record.getFTimetz());
            Assertions.assertNotNull(record.getFTimestamp());
            Assertions.assertNotNull(record.getFTimestamptz());
            Assertions.assertNotNull(record.getFInterval());
            Assertions.assertEquals(10, record.getFInterval().getYears());
            Assertions.assertEquals(3, record.getFInterval().getMonths());
            Assertions.assertEquals(332, record.getFInterval().getDays());
        });
        flag.flag();
    }

}
