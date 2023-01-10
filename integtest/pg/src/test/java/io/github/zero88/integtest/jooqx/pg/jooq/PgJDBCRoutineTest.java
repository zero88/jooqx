package io.github.zero88.integtest.jooqx.pg.jooq;

import java.util.Arrays;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.github.zero88.integtest.jooqx.pg.DupResultConverter;
import io.github.zero88.integtest.jooqx.pg.PgUseJooqType;
import io.github.zero88.jooqx.DSLAdapter;
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
import io.vertx.core.Vertx;
import io.vertx.jdbcclient.JDBCPool;
import io.vertx.junit5.Checkpoint;
import io.vertx.junit5.VertxTestContext;

class PgJDBCRoutineTest extends PgSQLJooqxTest<JDBCPool>
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
            Assertions.assertEquals(7, rec.getF1());
            Assertions.assertEquals("7 is text", rec.getF2());
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
        jooqx.routineResult(dup).onSuccess(rr -> ctx.verify(() -> {
            System.out.println(rr);
            Assertions.assertEquals("[\"f1\", \"f2\"]", Arrays.toString(rr.getOutputFields()));
            Assertions.assertNotNull(rr.getRecord());
            Assertions.assertEquals(2, rr.getRecord().fields().length);
            Assertions.assertEquals(5, rr.getRecord().get(rr.getOutputFields()[0]));
            Assertions.assertEquals("5 is text", rr.getRecord().get(rr.getOutputFields()[1]));
            cp.flag();
        })).onFailure(ctx::failNow);
    }

    @Test
    void test_fn_returns_resultSet(VertxTestContext ctx) {
        Checkpoint cp = ctx.checkpoint(1);
        jooqx.fetchMany(dsl -> dsl.selectFrom(Tables.FIND_AUTHORS.call(""))).onSuccess(rows -> {
            System.out.println(rows);
            Assertions.assertEquals(8, rows.size());
            FindAuthorsRecord record = rows.get(0);
            Assertions.assertEquals(1, record.getId());
            cp.flag();
        }).onFailure(ctx::failNow);
    }

}
