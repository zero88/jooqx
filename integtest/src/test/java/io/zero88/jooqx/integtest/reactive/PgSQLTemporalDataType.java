package io.zero88.jooqx.integtest.reactive;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.vertx.core.Vertx;
import io.vertx.junit5.Checkpoint;
import io.vertx.junit5.VertxTestContext;
import io.vertx.pgclient.PgPool;
import io.zero88.jooqx.ReactiveDSL;
import io.zero88.jooqx.integtest.PostgreSQLHelper;
import io.zero88.jooqx.integtest.PostgreSQLHelper.UsePgSQLErrorConverter;
import io.zero88.jooqx.integtest.pgsql.tables.TemporalDataType;
import io.zero88.jooqx.integtest.pgsql.tables.records.TemporalDataTypeRecord;
import io.zero88.jooqx.spi.PostgreSQLReactiveTest.PostgreSQLPoolTest;

class PgSQLTemporalDataType extends PostgreSQLPoolTest implements UsePgSQLErrorConverter<PgPool>, PostgreSQLHelper {

    @Override
    @BeforeEach
    public void tearUp(Vertx vertx, VertxTestContext ctx) {
        super.tearUp(vertx, ctx);
        this.prepareDatabase(ctx, this, connOpt, "pg_datatype/temporal.sql");
    }

    @Test
    void testTemporal(VertxTestContext ctx) {
        final Checkpoint flag = ctx.checkpoint();
        final TemporalDataType table = catalog().PUBLIC.TEMPORAL_DATA_TYPE;
        jooqx.execute(jooqx.dsl().selectFrom(table), ReactiveDSL.adapter().fetchMany(table), ar -> {
            final List<TemporalDataTypeRecord> records = assertResultSize(ctx, flag, ar, 4);
        });
    }

}
