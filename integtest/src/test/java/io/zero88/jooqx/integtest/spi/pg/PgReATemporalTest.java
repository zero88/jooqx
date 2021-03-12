package io.zero88.jooqx.integtest.spi.pg;

import java.util.Objects;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.vertx.core.Vertx;
import io.vertx.junit5.Checkpoint;
import io.vertx.junit5.VertxTestContext;
import io.vertx.pgclient.PgPool;
import io.zero88.jooqx.ReactiveDSL;
import io.zero88.jooqx.integtest.pgsql.tables.TemporalDataType;
import io.zero88.jooqx.integtest.pgsql.tables.records.TemporalDataTypeRecord;
import io.zero88.jooqx.spi.pg.PgPoolProvider;
import io.zero88.jooqx.spi.pg.PgSQLReactiveTest;
import io.zero88.jooqx.spi.pg.UsePgSQLErrorConverter;

class PgReATemporalTest extends PgSQLReactiveTest<PgPool>
    implements UsePgSQLErrorConverter, PgPoolProvider, PostgreSQLHelper {

    @Override
    @BeforeEach
    public void tearUp(Vertx vertx, VertxTestContext ctx) {
        super.tearUp(vertx, ctx);
        this.prepareDatabase(ctx, this, connOpt, "pg_datatype/temporal.sql");
    }

    @Test
    void queryTemporal(VertxTestContext ctx) {
        final Checkpoint flag = ctx.checkpoint();
        final TemporalDataType table = catalog().PUBLIC.TEMPORAL_DATA_TYPE;
        jooqx.execute(jooqx.dsl().selectFrom(table).limit(1), ReactiveDSL.adapter().fetchOne(table), ar -> {
            if (Objects.nonNull(ar.cause())) {
                ar.cause().printStackTrace();
            }
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

}
