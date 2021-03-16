package io.zero88.jooqx.integtest.spi.pg.jooq;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.vertx.core.Vertx;
import io.vertx.jdbcclient.JDBCPool;
import io.vertx.junit5.Checkpoint;
import io.vertx.junit5.VertxTestContext;
import io.zero88.jooqx.DSLAdapter;
import io.zero88.jooqx.UseJdbcErrorConverter;
import io.zero88.jooqx.integtest.pgsql.tables.TemporalDataType;
import io.zero88.jooqx.integtest.pgsql.tables.records.TemporalDataTypeRecord;
import io.zero88.jooqx.integtest.spi.pg.PostgreSQLHelper.PgUseJooqType;
import io.zero88.jooqx.spi.jdbc.JDBCReactiveProvider;
import io.zero88.jooqx.spi.pg.PgSQLReactiveTest;

class PgReAJDBCPoolTest extends PgSQLReactiveTest<JDBCPool>
    implements PgUseJooqType, JDBCReactiveProvider, UseJdbcErrorConverter {

    @Override
    @BeforeEach
    public void tearUp(Vertx vertx, VertxTestContext ctx) {
        super.tearUp(vertx, ctx);
        this.prepareDatabase(ctx, this, connOpt, "pg_data/temporal.sql");
    }

    @Test
    void queryTemporal(VertxTestContext ctx) {
        Checkpoint cp = ctx.checkpoint();
        final TemporalDataType table = catalog().PUBLIC.TEMPORAL_DATA_TYPE;
        jooqx.execute(jooqx.dsl().selectFrom(table).limit(1), DSLAdapter.fetchOne(table), ar -> {
            ctx.verify(() -> {
                final TemporalDataTypeRecord record = assertSuccess(ctx, ar);
                System.out.println(record);
                Assertions.assertNotNull(record.getDate());
                Assertions.assertNotNull(record.getTime());
                Assertions.assertNotNull(record.getTimestamptz());
                //TODO: Vertx bug #https://github.com/eclipse-vertx/vertx-sql-client/issues/918
                //                Assertions.assertNotNull(record.getTimestamp());
                //                Assertions.assertNotNull(record.getTimetz());
                //TODO: Should use converter with String as interval
                //                Assertions.assertNotNull(record.getInterval());
                cp.flag();
            });
        });
    }

}
