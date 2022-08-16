package io.github.zero88.integtest.jooqx.pg.jooq;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import io.github.zero88.integtest.jooqx.pg.JDBCIntervalConverter;
import io.github.zero88.integtest.jooqx.pg.PgUseJooqType;
import io.github.zero88.jooqx.BlockQuery;
import io.github.zero88.jooqx.DSLAdapter;
import io.github.zero88.jooqx.datatype.DataTypeMapperRegistry;
import io.github.zero88.jooqx.datatype.UserTypeAsJooqType;
import io.github.zero88.jooqx.spi.jdbc.JDBCErrorConverterProvider;
import io.github.zero88.jooqx.spi.jdbc.JDBCPoolHikariProvider;
import io.github.zero88.jooqx.spi.pg.PgSQLJooqxTest;
import io.github.zero88.sample.model.pgsql.tables.AllDataTypes;
import io.github.zero88.sample.model.pgsql.tables.records.AllDataTypesRecord;
import io.github.zero88.sample.model.pgsql.tables.records.AuthorsRecord;
import io.github.zero88.sample.model.pgsql.tables.records.BooksRecord;
import io.vertx.core.Vertx;
import io.vertx.jdbcclient.JDBCPool;
import io.vertx.junit5.Checkpoint;
import io.vertx.junit5.VertxTestContext;

//Fixed in https://github.com/vert-x3/vertx-jdbc-client/pull/235
class PgReAJDBCPoolTest extends PgSQLJooqxTest<JDBCPool>
    implements PgUseJooqType, JDBCPoolHikariProvider, JDBCErrorConverterProvider {

    @Override
    @BeforeEach
    public void tearUp(Vertx vertx, VertxTestContext ctx) {
        super.tearUp(vertx, ctx);
        this.prepareDatabase(ctx, this, connOpt, "pg_data/book_author.sql", "pg_data/temporal.sql");
    }

    @Override
    public DataTypeMapperRegistry typeMapperRegistry() {
        return PgUseJooqType.super.typeMapperRegistry().add(UserTypeAsJooqType.create(new JDBCIntervalConverter()));
    }

    @Test
    void queryTemporal(VertxTestContext ctx) {
        Checkpoint cp = ctx.checkpoint();
        final AllDataTypes table = schema().ALL_DATA_TYPES;

        jooqx.fetchOne(dsl -> dsl.selectFrom(table).where(table.ID.eq(31)).limit(1), ar -> ctx.verify(() -> {
            final AllDataTypesRecord record = assertSuccess(ctx, ar);
            System.out.println(record);
            Assertions.assertNotNull(record.getFDate());
            Assertions.assertNotNull(record.getFTime());
            Assertions.assertNotNull(record.getFTimestamp());
            Assertions.assertNotNull(record.getFTimetz());
            Assertions.assertNotNull(record.getFTimestamptz());
            //TODO: Should use converter with String as interval
            Assertions.assertNotNull(record.getFInterval());
            cp.flag();
        }));
    }

    @Test
    void test_select_block(VertxTestContext ctx) {
        final Checkpoint flag = ctx.checkpoint();
        jooqx.block(dsl -> BlockQuery.create()
                                     .add(dsl.selectFrom(schema().AUTHORS), DSLAdapter.fetchMany(schema().AUTHORS))
                                     .add(dsl.selectFrom(schema().BOOKS), DSLAdapter.fetchMany(schema().BOOKS)))
             .onSuccess(rs -> ctx.verify(() -> {
                 Assertions.assertEquals(2, rs.size());

                 final List<AuthorsRecord> records = rs.get(0);
                 Assertions.assertEquals(8, records.size());
                 System.out.println(records);

                 final List<BooksRecord> records2 = rs.get(1);
                 Assertions.assertEquals(7, records2.size());
                 System.out.println(records2);
                 flag.flag();
             }))
             .onFailure(ctx::failNow);
    }

}
