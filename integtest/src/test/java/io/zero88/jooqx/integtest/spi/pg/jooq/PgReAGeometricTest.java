package io.zero88.jooqx.integtest.spi.pg.jooq;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.vertx.core.Vertx;
import io.vertx.junit5.Checkpoint;
import io.vertx.junit5.VertxTestContext;
import io.vertx.pgclient.PgPool;
import io.zero88.jooqx.DSLAdapter;
import io.zero88.jooqx.integtest.pgsql.tables.GeometricDataType;
import io.zero88.jooqx.integtest.pgsql.tables.records.GeometricDataTypeRecord;
import io.zero88.jooqx.integtest.spi.pg.PostgreSQLHelper.PgUseJooqType;
import io.zero88.jooqx.spi.pg.PgPoolProvider;
import io.zero88.jooqx.spi.pg.PgSQLJooqxTest;
import io.zero88.jooqx.spi.pg.PgSQLErrorConverterProvider;

class PgReAGeometricTest extends PgSQLJooqxTest<PgPool>
    implements PgSQLErrorConverterProvider, PgPoolProvider, PgUseJooqType {

    @Override
    @BeforeEach
    public void tearUp(Vertx vertx, VertxTestContext ctx) {
        super.tearUp(vertx, ctx);
        this.prepareDatabase(ctx, this, connOpt, "pg_data/geometric.sql");
    }

    @Test
    void queryGeometric(VertxTestContext ctx) {
        final Checkpoint cp = ctx.checkpoint();
        final GeometricDataType table = schema().GEOMETRIC_DATA_TYPE;
        jooqx.execute(jooqx.dsl().selectFrom(table).limit(1), DSLAdapter.fetchOne(table), ar -> ctx.verify(() -> {
            final GeometricDataTypeRecord record = assertSuccess(ctx, ar);
            System.out.println(record);

            Assertions.assertNotNull(record.getPoint());

            Assertions.assertNotNull(record.getPolygon());
            Assertions.assertNotNull(record.getBox());

            Assertions.assertNotNull(record.getCircle());
            Assertions.assertNotNull(record.getLine());
            Assertions.assertNotNull(record.getLseg());
            Assertions.assertNotNull(record.getClosedpath());
            Assertions.assertNotNull(record.getOpenpath());
            cp.flag();
        }));
    }

}
