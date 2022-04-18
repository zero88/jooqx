package io.github.zero88.integtest.jooqx.pg.jooq;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.github.zero88.integtest.jooqx.pg.PostgreSQLHelper.PgUseJooqType;
import io.github.zero88.jooqx.DSLAdapter;
import io.github.zero88.jooqx.spi.pg.PgPoolProvider;
import io.github.zero88.jooqx.spi.pg.PgSQLErrorConverterProvider;
import io.github.zero88.jooqx.spi.pg.PgSQLJooqxTest;
import io.github.zero88.sample.model.pgsql.tables.GeometricDataType;
import io.github.zero88.sample.model.pgsql.tables.records.GeometricDataTypeRecord;
import io.vertx.core.Vertx;
import io.vertx.junit5.Checkpoint;
import io.vertx.junit5.VertxTestContext;
import io.vertx.pgclient.PgPool;

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
