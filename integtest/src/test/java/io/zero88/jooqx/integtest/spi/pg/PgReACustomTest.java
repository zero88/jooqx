package io.zero88.jooqx.integtest.spi.pg;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.vertx.core.Vertx;
import io.vertx.junit5.Checkpoint;
import io.vertx.junit5.VertxTestContext;
import io.vertx.pgclient.PgPool;
import io.zero88.jooqx.ReactiveDSL;
import io.zero88.jooqx.integtest.pgsql.tables.EnumDataType;
import io.zero88.jooqx.integtest.pgsql.tables.UdtDataType;
import io.zero88.jooqx.integtest.pgsql.tables.records.EnumDataTypeRecord;
import io.zero88.jooqx.integtest.pgsql.tables.records.UdtDataTypeRecord;
import io.zero88.jooqx.spi.pg.PgPoolProvider;
import io.zero88.jooqx.spi.pg.PgSQLReactiveTest;
import io.zero88.jooqx.spi.pg.UsePgSQLErrorConverter;

class PgReACustomTest extends PgSQLReactiveTest<PgPool>
    implements UsePgSQLErrorConverter, PgPoolProvider, PostgreSQLHelper {

    @Override
    @BeforeEach
    public void tearUp(Vertx vertx, VertxTestContext ctx) {
        super.tearUp(vertx, ctx);
        this.prepareDatabase(ctx, this, connOpt, "pg_data/custom.sql");
    }

    @Test
    void queryEnum(VertxTestContext ctx) {
        Checkpoint flag = ctx.checkpoint();
        final EnumDataType table = catalog().PUBLIC.ENUM_DATA_TYPE;
        jooqx.execute(jooqx.dsl().selectFrom(table).limit(1), ReactiveDSL.adapter().fetchOne(table),
                      ar -> ctx.verify(() -> {
                          final EnumDataTypeRecord record = assertSuccess(ctx, ar);
                          System.out.println(record);

                          Assertions.assertNotNull(record.getCurrentmood());

                          Assertions.assertNotNull(record.getCurrentweather());
                          flag.flag();
                      }));
    }

    @Test
    void queryCustom(VertxTestContext ctx) {
        Checkpoint flag = ctx.checkpoint();
        final UdtDataType table = catalog().PUBLIC.UDT_DATA_TYPE;
        jooqx.execute(jooqx.dsl().selectFrom(table).limit(1), ReactiveDSL.adapter().fetchOne(table),
                      ar -> ctx.verify(() -> {
                          final UdtDataTypeRecord record = assertSuccess(ctx, ar);
                          System.out.println(record);
                          Assertions.assertNotNull(record.getAddress());
                          flag.flag();
                      }));
    }

}
