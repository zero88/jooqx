package io.github.zero88.integtest.jooqx.pg.jooq;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.util.Arrays;
import java.util.List;

import org.jooq.types.YearToSecond;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
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
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;

//Fixed in https://github.com/vert-x3/vertx-jdbc-client/pull/235
class PgJDBCQueryTest extends PgSQLJooqxTest<JDBCPool>
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
    void test_simple(VertxTestContext ctx) {
        Checkpoint cp = ctx.checkpoint();
        jooqx.sqlClient()
             .query("select '[\"test\"]'::jsonb")
             .execute()
             .onFailure(ctx::failNow)
             .onSuccess(res -> ctx.verify(() -> {
                 final RowSet<Row> value = res.value();
                 Assertions.assertInstanceOf(String.class, value.iterator().next().getValue(0));
                 cp.flag();
             }));
    }

    @Test
    void test_insert_temporal(VertxTestContext ctx) {
        Checkpoint cp = ctx.checkpoint();
        final AllDataTypes table = schema().ALL_DATA_TYPES;

        jooqx.execute(dsl -> dsl.insertInto(table)
                                .columns(table.ID, table.F_DATE, table.F_TIME, table.F_TIMETZ, table.F_TIMESTAMP,
                                         table.F_TIMESTAMPTZ, table.F_INTERVAL)
                                .values(Arrays.asList(36, LocalDate.parse("2022-05-30"), LocalTime.parse("18:00:00"),
                                                      OffsetTime.parse("06:00:00+02:00"),
                                                      LocalDateTime.parse("2022-05-14T07:00:00"),
                                                      OffsetDateTime.parse("2022-05-14T07:00:00-02:00"),
                                                      YearToSecond.valueOf("+1-3 +5 07:09:10.002000000"))),
                      DSLAdapter.fetchCount())
             .flatMap(i -> jooqx.fetchOne(dsl -> dsl.selectFrom(table).where(table.ID.eq(36))))
             .onFailure(ctx::failNow)
             .onSuccess(record -> ctx.verify(() -> {
                 System.out.println(record);
                 Assertions.assertEquals(LocalDate.parse("2022-05-30"), record.getFDate());
                 Assertions.assertEquals(LocalTime.parse("18:00:00"), record.getFTime());
                 Assertions.assertEquals(OffsetTime.parse("04:00:00Z"), record.getFTimetz());
                 Assertions.assertEquals(LocalDateTime.parse("2022-05-14T07:00:00"), record.getFTimestamp());
                 Assertions.assertEquals(OffsetDateTime.parse("2022-05-14T09:00:00Z"), record.getFTimestamptz());
                 Assertions.assertEquals("+1-3 +5 07:09:10.002000000", record.getFInterval().toString());
                 cp.flag();
             }));
    }

    @Test
    void test_query_temporal(VertxTestContext ctx) {
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
    void test_block_select(VertxTestContext ctx) {
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
