package io.github.zero88.integtest.jooqx.pg.jooq;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.util.Arrays;
import java.util.Optional;

import org.jooq.DSLContext;
import org.jooq.Parameter;
import org.jooq.Results;
import org.jooq.impl.DSL;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.github.zero88.integtest.jooqx.pg.PgUseJooqType;
import io.github.zero88.jooqx.JooqDSLProvider;
import io.github.zero88.jooqx.JooqSQL;
import io.github.zero88.jooqx.spi.jdbc.JDBCErrorConverterProvider;
import io.github.zero88.jooqx.spi.jdbc.JDBCPoolHikariProvider;
import io.github.zero88.jooqx.spi.pg.PgSQLJooqxTest;
import io.github.zero88.sample.model.pgsql.Tables;
import io.github.zero88.sample.model.pgsql.routines.Add;
import io.github.zero88.sample.model.pgsql.routines.Dup;
import io.github.zero88.sample.model.pgsql.routines.Dup2;
import io.github.zero88.sample.model.pgsql.routines.RemoveAuthor;
import io.github.zero88.sample.model.pgsql.tables.AllDataTypes;
import io.github.zero88.sample.model.pgsql.tables.records.AllDataTypesRecord;
import io.github.zero88.utils.Strings;
import io.vertx.core.Vertx;
import io.vertx.jdbcclient.JDBCPool;
import io.vertx.jdbcclient.SqlOutParam;
import io.vertx.junit5.Checkpoint;
import io.vertx.junit5.VertxTestContext;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.Tuple;

import com.zaxxer.hikari.HikariDataSource;

class PgJooqTest extends PgSQLJooqxTest<JDBCPool>
    implements PgUseJooqType, JDBCPoolHikariProvider, JDBCErrorConverterProvider {

    private HikariDataSource dataSource;
    private DSLContext _dsl;

    @Override
    @BeforeEach
    public void tearUp(Vertx vertx, VertxTestContext ctx) {
        super.tearUp(vertx, ctx);
        this.prepareDatabase(ctx, this, connOpt, "pg_data/book_author.sql", "pg_data/temporal.sql");
        dataSource = this.createDataSource(connOpt);
        _dsl       = JooqDSLProvider.create(dialect(), dataSource).dsl();
    }

    @Override
    @AfterEach
    public void tearDown(Vertx vertx, VertxTestContext ctx) {
        super.tearDown(vertx, ctx);
        dataSource.close();
    }

    @Test
    void test_fn_returns_value(VertxTestContext ctx) {
        Checkpoint cp = ctx.checkpoint(1);

        final Add add = new Add();
        add.set__1(1);
        add.set__2(2);
        JooqSQL.printJooqRoutine(_dsl, add);
        System.out.println(add.asAggregateFunction());

        System.out.println("EXECUTE VIA JOOQ.................");
        System.out.println("EXECUTE STATUS:         " + add.execute(_dsl.configuration()));
        System.out.println("RETURN VALUE:           " + add.getReturnValue() + "::" +
                           Optional.ofNullable(add.getReturnValue()).map(Object::getClass).orElse(null));
        System.out.println("RESULTS:                " + add.getResults() + "::" + add.getResults().getClass());
        cp.flag();
    }

    @Test
    void test_fn_returns_2_outParams(VertxTestContext ctx) {
        Checkpoint cp = ctx.checkpoint(2);

        final Dup dup = new Dup();
        dup.set__1(2);
        JooqSQL.printJooqRoutine(_dsl, dup);
        System.out.println("EXECUTE VIA JOOQ.................");
        System.out.println("EXECUTE STATUS:         " + dup.execute(_dsl.configuration()));
        System.out.println("RETURN VALUE:           " + dup.getReturnValue() + "::" +
                           Optional.ofNullable(dup.getReturnValue()).map(Object::getClass).orElse(null));
        System.out.println("RESULTS:                " + dup.getResults() + "::" + dup.getResults().getClass());
        for (int i = 0; i < dup.getOutParameters().size(); i++) {
            Parameter<?> p = dup.getOutParameters().get(i);
            Object v = dup.getValue(p);
            System.out.println("RETURN VALUE [" + i + "]:       " + v + "::" + v.getClass());
        }
        cp.flag();

        // Plain vertx
        System.out.println("EXECUTE VIA PLAIN VERTX.................");
        jooqx.sqlClient()
             .preparedQuery(_dsl.render(dup))
             .execute(Tuple.of(2, SqlOutParam.OUT(Dup.F1.getDataType().getSQLType()),
                               SqlOutParam.OUT(Dup.F2.getDataType().getSQLType())))
             .onSuccess(rows -> ctx.verify(() -> {
                 System.out.println("Row count:             " + rows.rowCount());
                 System.out.println("Row size:              " + rows.size());
                 System.out.println("Column name:           " + rows.columnsNames());
                 System.out.println("Column descriptors:    " + rows.columnDescriptors());
                 for (Row row : rows) {
                     System.out.println("Row column size:       " + row.size());
                     for (int i = 0; i < row.size(); i++) {
                         System.out.println(
                             "RETURN VALUE [" + i + "]:      " + row.getValue(i) + "::" + row.getValue(i).getClass());
                     }
                 }
                 System.out.println(Strings.duplicate("=", 50));
                 cp.flag();
             }))
             .onFailure(ctx::failNow);
    }

    @Test
    void test_fn_returns_udt(VertxTestContext ctx) {
        Checkpoint cp = ctx.checkpoint(1);

        final Dup2 dup2 = new Dup2();
        dup2.set__1(5);
        JooqSQL.printJooqRoutine(_dsl, dup2);
        System.out.println(dup2.asAggregateFunction());

        System.out.println("EXECUTE VIA JOOQ.................");
        System.out.println("EXECUTE STATUS:         " + dup2.execute(_dsl.configuration()));
        System.out.println("RETURN VALUE:           " + dup2.getReturnValue() + "::" +
                           Optional.ofNullable(dup2.getReturnValue()).map(Object::getClass).orElse(null));
        System.out.println("RESULTS:                " + dup2.getResults() + "::" + dup2.getResults().getClass());
        cp.flag();
    }

    @Test
    void test_fn_returns_void(VertxTestContext ctx) {
        Checkpoint cp = ctx.checkpoint(1);

        final RemoveAuthor routine = new RemoveAuthor();
        routine.setAuthorName("J.D. Salinger");
        JooqSQL.printJooqRoutine(_dsl, routine);

        System.out.println("EXECUTE VIA JOOQ.................");
        System.out.println("EXECUTE STATUS:         " + routine.execute(_dsl.configuration()));
        System.out.println("RETURN VALUE:           " + routine.getReturnValue() + "::" +
                           Optional.ofNullable(routine.getReturnValue()).map(Object::getClass).orElse(null));
        System.out.println("RESULTS:                " + routine.getResults() + "::" + routine.getResults().getClass());
        cp.flag();
    }

    @Test
    void test_block_insert(VertxTestContext ctx) {
        final Checkpoint cp = ctx.checkpoint(1);
        final int execute = _dsl.begin(
            _dsl.insertInto(schema().AUTHORS, schema().AUTHORS.ID, schema().AUTHORS.NAME, schema().AUTHORS.COUNTRY)
                .values(Arrays.asList(DSL.defaultValue(Tables.AUTHORS.ID), "abc", "xyz")),
            _dsl.insertInto(schema().AUTHORS, schema().AUTHORS.ID, schema().AUTHORS.NAME, schema().AUTHORS.COUNTRY)
                .values(Arrays.asList(DSL.defaultValue(Tables.AUTHORS.ID), "abc1", "xyz1"))).execute();
        Assertions.assertEquals(0, execute);
        Assertions.assertEquals(10, _dsl.selectCount().from(schema().AUTHORS).fetchOne().get(0));
        cp.flag();
    }

    @Test
    void test_block_select(VertxTestContext ctx) {
        final Checkpoint cp = ctx.checkpoint();
        final Results results = _dsl.queries(_dsl.selectFrom(schema().AUTHORS), _dsl.selectFrom(schema().BOOKS))
                                    .fetchMany();
        results.forEach(records -> {
            System.out.println(records.recordType());
            System.out.println(records);
        });
        cp.flag();
    }

    @Test
    void test_query_temporal(VertxTestContext ctx) {
        final Checkpoint flag = ctx.checkpoint();
        final DSLContext dsl = JooqDSLProvider.create(dialect(), createDataSource(connOpt)).dsl();
        final AllDataTypes table = schema().ALL_DATA_TYPES;
        final AllDataTypesRecord record = dsl.selectFrom(table).where(table.ID.eq(31)).fetchOne();
        ctx.verify(() -> {
            System.out.println(record);
            Assertions.assertNotNull(record);
            Assertions.assertEquals(LocalDate.parse("2022-05-30"), record.getFDate());
            Assertions.assertEquals(LocalTime.parse("18:00:00"), record.getFTime());
            Assertions.assertEquals(OffsetTime.parse("06:00:00+02:00"), record.getFTimetz());
            Assertions.assertEquals(LocalDateTime.parse("2022-05-14T07:00:00"), record.getFTimestamp());
            Assertions.assertEquals(Instant.parse("2022-05-14T09:00:00Z"), record.getFTimestamptz().toInstant());
            Assertions.assertNotNull(record.getFInterval());
            Assertions.assertEquals(10, record.getFInterval().getYears());
            Assertions.assertEquals(3, record.getFInterval().getMonths());
            Assertions.assertEquals(332, record.getFInterval().getDays());
        });
        flag.flag();
    }

    @Test
    void test_insert_temporal(VertxTestContext ctx) {
        final Checkpoint flag = ctx.checkpoint();
        final DSLContext dsl = JooqDSLProvider.create(dialect(), createDataSource(connOpt)).dsl();
        final AllDataTypes table = schema().ALL_DATA_TYPES;
        final AllDataTypesRecord record = dsl.insertInto(table)
                                             .columns(table.ID, table.F_DATE, table.F_TIME, table.F_TIMETZ,
                                                      table.F_TIMESTAMP, table.F_TIMESTAMPTZ)
                                             .values(Arrays.asList(36, LocalDate.parse("2022-05-30"),
                                                                   LocalTime.parse("18:00:00"),
                                                                   OffsetTime.parse("06:00:00+02:00"),
                                                                   LocalDateTime.parse("2022-05-14T07:00:00"),
                                                                   OffsetDateTime.parse("2022-05-14T07:00:00-02:00")))
                                             .returning()
                                             .fetchOne();
        ctx.verify(() -> {
            System.out.println(record);
            Assertions.assertNotNull(record);
            Assertions.assertEquals(LocalDate.parse("2022-05-30"), record.getFDate());
            Assertions.assertEquals(LocalTime.parse("18:00:00"), record.getFTime());
            Assertions.assertEquals(OffsetTime.parse("06:00:00+02:00"), record.getFTimetz());
            Assertions.assertEquals(LocalDateTime.parse("2022-05-14T07:00:00"), record.getFTimestamp());
            Assertions.assertEquals(Instant.parse("2022-05-14T09:00:00Z"), record.getFTimestamptz().toInstant());
        });
        flag.flag();
    }

}
