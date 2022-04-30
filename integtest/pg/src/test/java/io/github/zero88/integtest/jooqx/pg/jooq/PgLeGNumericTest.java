package io.github.zero88.integtest.jooqx.pg.jooq;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.github.zero88.integtest.jooqx.pg.PostgreSQLHelper.PgLegacyType;
import io.github.zero88.jooqx.DSLAdapter;
import io.github.zero88.jooqx.spi.pg.PgSQLLegacyTest;
import io.github.zero88.sample.model.pgsql.tables.AllDataTypes;
import io.github.zero88.sample.model.pgsql.tables.records.AllDataTypesRecord;
import io.vertx.core.Vertx;
import io.vertx.junit5.Checkpoint;
import io.vertx.junit5.VertxTestContext;

class PgLeGNumericTest extends PgSQLLegacyTest implements PgLegacyType {

    @BeforeEach
    @Override
    public void tearUp(Vertx vertx, VertxTestContext ctx) {
        super.tearUp(vertx, ctx);
        this.prepareDatabase(ctx, this, connOpt, "pg_data/numeric.sql");
    }

    @Test
    void queryNumeric(VertxTestContext ctx) {
        Checkpoint cp = ctx.checkpoint();
        final AllDataTypes table = schema().ALL_DATA_TYPES;
        jooqx.execute(dsl -> dsl.selectFrom(table).where(table.ID.eq(1)).limit(1), DSLAdapter.fetchOne(table),
                      ar -> ctx.verify(() -> {
                          final AllDataTypesRecord record = assertSuccess(ctx, ar);
                          System.out.println(record);

                          Assertions.assertNotNull(record.getFBoolean());

                          Assertions.assertNotNull(record.getFNumInt());
                          Assertions.assertNotNull(record.getFNumLong());

                          Assertions.assertNotNull(record.getFNumFloat4());
                          Assertions.assertNotNull(record.getFNumDouble());

                          Assertions.assertNotNull(record.getFNumSerial());
                          Assertions.assertNotNull(record.getFNumBigSerial());
                          Assertions.assertNotNull(record.getFNumSmallSerial());

                          Assertions.assertNotNull(record.getFNumShort());
                          Assertions.assertNotNull(record.getFNumNumeric());
                          cp.flag();
                      }));
    }

}
