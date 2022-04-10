package io.github.zero88.integtest.jooqx.pg.jooq;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.github.zero88.integtest.jooqx.pg.PostgreSQLHelper.PgUseJooqType;
import io.github.zero88.jooqx.DSLAdapter;
import io.github.zero88.jooqx.datatype.DataTypeMapperRegistry;
import io.github.zero88.jooqx.datatype.UserTypeAsJooqType;
import io.github.zero88.jooqx.spi.jdbc.JDBCErrorConverterProvider;
import io.github.zero88.jooqx.spi.jdbc.JDBCPoolHikariProvider;
import io.github.zero88.jooqx.spi.pg.PgSQLJooqxTest;
import io.vertx.core.Vertx;
import io.vertx.jdbcclient.JDBCPool;
import io.vertx.junit5.Checkpoint;
import io.vertx.junit5.VertxTestContext;
import io.zero88.sample.data.pgsql.tables.TemporalDataType;
import io.zero88.sample.data.pgsql.tables.records.TemporalDataTypeRecord;

//TODO Fix in https://github.com/vert-x3/vertx-jdbc-client/pull/235
//TODO: Vertx bug #https://github.com/eclipse-vertx/vertx-sql-client/issues/918
class PgReAJDBCPoolTest extends PgSQLJooqxTest<JDBCPool>
    implements PgUseJooqType, JDBCPoolHikariProvider, JDBCErrorConverterProvider {

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
                                               UserTypeAsJooqType.create(new JDBCIntervalConverter()));
    }

    @Test
    void queryTemporal(VertxTestContext ctx) {
        Checkpoint cp = ctx.checkpoint();
        final TemporalDataType table = schema().TEMPORAL_DATA_TYPE;

        jooqx.execute(dsl -> dsl.selectFrom(table).limit(1), DSLAdapter.fetchOne(table), ar -> ctx.verify(() -> {
            final TemporalDataTypeRecord record = assertSuccess(ctx, ar);
            System.out.println(record);
            Assertions.assertNotNull(record.getDate());
            Assertions.assertNotNull(record.getTime());
            Assertions.assertNotNull(record.getTimestamp());
            Assertions.assertNotNull(record.getTimetz());
            Assertions.assertNotNull(record.getTimestamptz());
            //TODO: Should use converter with String as interval
            Assertions.assertNotNull(record.getInterval());
            cp.flag();
        }));
    }

}
