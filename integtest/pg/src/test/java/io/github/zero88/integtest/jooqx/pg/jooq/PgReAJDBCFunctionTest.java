package io.github.zero88.integtest.jooqx.pg.jooq;

import java.util.Arrays;
import java.util.Optional;

import org.jooq.DSLContext;
import org.jooq.Parameter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.github.zero88.integtest.jooqx.pg.DupResultConverter;
import io.github.zero88.integtest.jooqx.pg.PgUseJooqType;
import io.github.zero88.jooqx.DSLAdapter;
import io.github.zero88.jooqx.JooqDSLProvider;
import io.github.zero88.jooqx.JooqSQL;
import io.github.zero88.jooqx.datatype.DataTypeMapperRegistry;
import io.github.zero88.jooqx.datatype.UserTypeAsJooqType;
import io.github.zero88.jooqx.spi.jdbc.JDBCErrorConverterProvider;
import io.github.zero88.jooqx.spi.jdbc.JDBCPoolHikariProvider;
import io.github.zero88.jooqx.spi.pg.PgSQLJooqxTest;
import io.github.zero88.sample.model.pgsql.Tables;
import io.github.zero88.sample.model.pgsql.routines.Add;
import io.github.zero88.sample.model.pgsql.routines.Dup;
import io.github.zero88.sample.model.pgsql.routines.Dup2;
import io.github.zero88.sample.model.pgsql.routines.RemoveAuthor;
import io.github.zero88.sample.model.pgsql.tables.records.FindAuthorsRecord;
import io.github.zero88.sample.model.pgsql.udt.records.DupResultRecord;
import io.github.zero88.utils.Strings;
import io.vertx.core.Vertx;
import io.vertx.jdbcclient.JDBCPool;
import io.vertx.jdbcclient.SqlOutParam;
import io.vertx.junit5.Checkpoint;
import io.vertx.junit5.VertxTestContext;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.Tuple;

import com.zaxxer.hikari.HikariDataSource;

class PgReAJDBCFunctionTest extends PgSQLJooqxTest<JDBCPool>
    implements PgUseJooqType, JDBCPoolHikariProvider, JDBCErrorConverterProvider {

    @Override
    @BeforeEach
    public void tearUp(Vertx vertx, VertxTestContext ctx) {
        super.tearUp(vertx, ctx);
        this.prepareDatabase(ctx, this, connOpt, "pg_data/book_author.sql");
    }

    @Override
    public DataTypeMapperRegistry typeMapperRegistry() {
        return PgUseJooqType.super.typeMapperRegistry().add(UserTypeAsJooqType.create(new DupResultConverter()));
    }

    @Test
    void test_jooq_fn_returns_value(VertxTestContext ctx) {
        Checkpoint cp = ctx.checkpoint(1);
        final HikariDataSource dataSource = this.createDataSource(connOpt);
        final DSLContext _dsl = JooqDSLProvider.create(dialect(), dataSource).dsl();

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
        dataSource.close();
        cp.flag();
    }

    @Test
    void test_jooq_fn_returns_2_outParams(VertxTestContext ctx) {
        Checkpoint cp = ctx.checkpoint(2);
        final HikariDataSource dataSource = this.createDataSource(connOpt);
        final JooqDSLProvider dslProvider = JooqDSLProvider.create(dialect(), dataSource);
        final DSLContext _dsl = dslProvider.dsl();

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
        dataSource.close();
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
    void test_jooq_fn_returns_udt(VertxTestContext ctx) {
        Checkpoint cp = ctx.checkpoint(1);
        final HikariDataSource dataSource = this.createDataSource(connOpt);
        final DSLContext _dsl = JooqDSLProvider.create(dialect(), dataSource).dsl();

        final Dup2 dup2 = new Dup2();
        dup2.set__1(5);
        JooqSQL.printJooqRoutine(_dsl, dup2);
        System.out.println(dup2.asAggregateFunction());

        System.out.println("EXECUTE VIA JOOQ.................");
        System.out.println("EXECUTE STATUS:         " + dup2.execute(_dsl.configuration()));
        System.out.println("RETURN VALUE:           " + dup2.getReturnValue() + "::" +
                           Optional.ofNullable(dup2.getReturnValue()).map(Object::getClass).orElse(null));
        System.out.println("RESULTS:                " + dup2.getResults() + "::" + dup2.getResults().getClass());
        dataSource.close();
        cp.flag();
    }

    @Test
    void test_jooq_fn_returns_void(VertxTestContext ctx) {
        Checkpoint cp = ctx.checkpoint(1);
        final HikariDataSource dataSource = this.createDataSource(connOpt);
        final JooqDSLProvider dslProvider = JooqDSLProvider.create(dialect(), dataSource);
        final DSLContext _dsl = dslProvider.dsl();

        final RemoveAuthor routine = new RemoveAuthor();
        routine.setAuthorName("J.D. Salinger");
        JooqSQL.printJooqRoutine(_dsl, routine);

        System.out.println("EXECUTE VIA JOOQ.................");
        System.out.println("EXECUTE STATUS:         " + routine.execute(_dsl.configuration()));
        System.out.println("RETURN VALUE:           " + routine.getReturnValue() + "::" +
                           Optional.ofNullable(routine.getReturnValue()).map(Object::getClass).orElse(null));
        System.out.println("RESULTS:                " + routine.getResults() + "::" + routine.getResults().getClass());
        dataSource.close();
        cp.flag();
    }

    @Test
    void test_fn_returns_value(VertxTestContext ctx) {
        Checkpoint cp = ctx.checkpoint();
        final Add add3 = new Add();
        add3.set__1(5);
        add3.set__2(10);
        jooqx.routine(add3).onSuccess(output -> ctx.verify(() -> {
            Assertions.assertEquals(15, output);
            cp.flag();
        })).onFailure(ctx::failNow);
    }

    @Test
    void test_fn_returns_udt(VertxTestContext ctx) {
        final Checkpoint cp = ctx.checkpoint();
        final Dup2 routine = new Dup2();
        routine.set__1(7);
        jooqx.routine(routine).onSuccess(rec -> ctx.verify(() -> {
            System.out.println(rec);
            Assertions.assertEquals(DupResultRecord.class, rec.getClass());
            Assertions.assertEquals(rec.getF1(), 7);
            Assertions.assertEquals(rec.getF2(), "7 is text");
            cp.flag();
        })).onFailure(ctx::failNow);
    }

    @Test
    void test_fn_returns_void(VertxTestContext ctx) {
        final Checkpoint cp = ctx.checkpoint();
        final String author = "Jane Austen";
        final RemoveAuthor routine = new RemoveAuthor();
        routine.setAuthorName(author);
        jooqx.routine(routine)
             .flatMap(ignore -> jooqx.execute(dsl -> dsl.selectOne()
                                                        .whereExists(dsl.selectFrom(Tables.AUTHORS)
                                                                        .where(Tables.AUTHORS.NAME.eq(author))),
                                              DSLAdapter.fetchExists()))
             .onSuccess(isExisted -> ctx.verify(() -> {
                 Assertions.assertFalse(isExisted);
                 cp.flag();
             }))
             .onFailure(ctx::failNow);
    }

    @Test
    void test_fn_returns_outParams(VertxTestContext ctx) {
        final Checkpoint cp = ctx.checkpoint();
        final Dup dup = new Dup();
        dup.set__1(5);
        jooqx.routineResult(dup)
             .onSuccess(rr -> ctx.verify(() -> {
                 System.out.println(rr);
                 Assertions.assertEquals("[\"f1\", \"f2\"]", Arrays.toString(rr.getOutputFields()));
                 Assertions.assertNotNull(rr.getRecord());
                 Assertions.assertEquals(2, rr.getRecord().fields().length);
                 Assertions.assertEquals(5, rr.getRecord().get(rr.getOutputFields()[0]));
                 Assertions.assertEquals("5 is text", rr.getRecord().get(rr.getOutputFields()[1]));
                 cp.flag();
             }))
             .onFailure(ctx::failNow);
    }

    @Test
    void test_fn_returns_resultSet(VertxTestContext ctx) {
        Checkpoint cp = ctx.checkpoint(1);
        jooqx.execute(dsl -> dsl.selectFrom(Tables.FIND_AUTHORS.call("")), DSLAdapter.fetchMany(Tables.FIND_AUTHORS))
             .onSuccess(rows -> {
                 System.out.println(rows);
                 Assertions.assertEquals(8, rows.size());
                 FindAuthorsRecord record = rows.get(0);
                 Assertions.assertEquals(1, record.getId());
                 cp.flag();
             })
             .onFailure(ctx::failNow);
    }

}
