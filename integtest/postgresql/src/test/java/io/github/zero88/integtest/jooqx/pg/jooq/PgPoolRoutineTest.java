package io.github.zero88.integtest.jooqx.pg.jooq;

import java.util.Arrays;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.github.zero88.integtest.jooqx.pg.DupResultConverter;
import io.github.zero88.integtest.jooqx.pg.PgUseJooqType;
import io.github.zero88.jooqx.datatype.DataTypeMapperRegistry;
import io.github.zero88.jooqx.datatype.UserTypeAsJooqType;
import io.github.zero88.jooqx.spi.pg.PgPoolProvider;
import io.github.zero88.jooqx.spi.pg.PgSQLErrorConverterProvider;
import io.github.zero88.jooqx.spi.pg.PgSQLJooqxTest;
import io.github.zero88.sample.model.pgsql.Tables;
import io.github.zero88.sample.model.pgsql.routines.Add;
import io.github.zero88.sample.model.pgsql.routines.Dup;
import io.github.zero88.sample.model.pgsql.routines.Dup2;
import io.github.zero88.sample.model.pgsql.routines.RemoveAuthor;
import io.github.zero88.sample.model.pgsql.tables.records.FindAuthorsRecord;
import io.github.zero88.sample.model.pgsql.udt.records.DupResultRecord;
import io.vertx.core.Vertx;
import io.vertx.junit5.Checkpoint;
import io.vertx.junit5.VertxTestContext;
import io.vertx.pgclient.PgPool;

class PgPoolRoutineTest extends PgSQLJooqxTest<PgPool>
    implements PgPoolProvider, PgUseJooqType, PgSQLErrorConverterProvider {

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
        final Add routine = new Add();
        routine.set__1(4);
        routine.set__2(8);
        jooqx.routine(routine).onSuccess(r -> ctx.verify(() -> {
            Assertions.assertEquals(12, r);
            cp.flag();
        })).onFailure(ctx::failNow);
    }

    @Test
    void test_fn_returns_outParams(VertxTestContext ctx) {
        Checkpoint cp = ctx.checkpoint();
        Dup routine = new Dup();
        routine.set__1(4);
        jooqx.routineResult(routine).onSuccess(rr -> ctx.verify(() -> {
            System.out.println(rr);
            Assertions.assertEquals("[\"f1\", \"f2\"]", Arrays.toString(rr.getOutputFields()));
            Assertions.assertNotNull(rr.getRecord());
            Assertions.assertEquals(2, rr.getRecord().fields().length);
            Assertions.assertEquals(4, rr.getRecord().get(rr.getOutputFields()[0]));
            Assertions.assertEquals("4 is text", rr.getRecord().get(rr.getOutputFields()[1]));
            cp.flag();
        })).onFailure(ctx::failNow);
    }

    @Test
    void test_fn_returns_udt(VertxTestContext ctx) {
        Checkpoint cp = ctx.checkpoint();
        final Dup2 routine = new Dup2();
        routine.set__1(5);
        jooqx.routine(routine).onSuccess(rec -> ctx.verify(() -> {
            System.out.println(rec);
            Assertions.assertEquals(DupResultRecord.class, rec.getClass());
            Assertions.assertEquals(rec.getF1(), 5);
            Assertions.assertEquals(rec.getF2(), "5 is text");
            cp.flag();
        })).onFailure(ctx::failNow);
    }

    @Test
    void test_fn_returns_void(VertxTestContext ctx) {
        Checkpoint cp = ctx.checkpoint(3);
        final String name = "J.D. Salinger";
        final RemoveAuthor routine = new RemoveAuthor();
        routine.setAuthorName(name);
        jooqx.routine(routine)
             .onSuccess(r -> ctx.verify(cp::flag))
             .flatMap(r -> jooqx.fetchExists(dsl -> dsl.selectFrom(Tables.AUTHORS).where(Tables.AUTHORS.NAME.eq(name))))
             .onSuccess(r -> ctx.verify(() -> {
                 Assertions.assertFalse(r);
                 cp.flag();
             }))
             .flatMap(r -> jooqx.fetchCount(dsl -> dsl.selectFrom(Tables.BOOKS)))
             .onSuccess(r -> ctx.verify(() -> {
                 Assertions.assertEquals(4, r);
                 cp.flag();
             }))
             .onFailure(ctx::failNow);
    }

    @Test
    void test_fn_returns_resultSet(VertxTestContext ctx) {
        Checkpoint cp = ctx.checkpoint();
        jooqx.fetchMany(dsl -> dsl.selectFrom(Tables.FIND_AUTHORS.call("Sc"))).onSuccess(rows -> ctx.verify(() -> {
            System.out.println(rows);
            Assertions.assertEquals(2, rows.size());
            FindAuthorsRecord rec1 = rows.get(0);
            Assertions.assertEquals(2, rec1.getId());
            Assertions.assertEquals("F. Scott. Fitzgerald", rec1.getName());
            FindAuthorsRecord rec2 = rows.get(1);
            Assertions.assertEquals(4, rec2.getId());
            Assertions.assertEquals("Scott Hanselman", rec2.getName());
            cp.flag();
        })).onFailure(ctx::failNow);
    }

}
