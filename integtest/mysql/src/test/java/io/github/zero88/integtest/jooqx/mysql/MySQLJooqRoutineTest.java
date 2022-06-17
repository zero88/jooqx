package io.github.zero88.integtest.jooqx.mysql;

import org.jooq.DSLContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.github.zero88.jooqx.JooqDSLProvider;
import io.github.zero88.jooqx.JooqSQL;
import io.github.zero88.jooqx.spi.jdbc.JDBCErrorConverterProvider;
import io.github.zero88.jooqx.spi.jdbc.JDBCPoolHikariProvider;
import io.github.zero88.jooqx.spi.mysql.MySQLJooqxTest;
import io.github.zero88.sample.model.mysql.Tables;
import io.github.zero88.sample.model.mysql.routines.CountAuthorByCountry;
import io.github.zero88.sample.model.mysql.routines.Hello;
import io.github.zero88.sample.model.mysql.routines.RemoveAuthor;
import io.github.zero88.sample.model.mysql.routines.SelectBooksByAuthor;
import io.vertx.core.Vertx;
import io.vertx.jdbcclient.JDBCPool;
import io.vertx.junit5.Checkpoint;
import io.vertx.junit5.VertxTestContext;

import com.zaxxer.hikari.HikariDataSource;

class MySQLJooqRoutineTest extends MySQLJooqxTest<JDBCPool>
    implements JDBCPoolHikariProvider, JDBCErrorConverterProvider, MySQLHelper {

    private HikariDataSource dataSource;
    private DSLContext _dsl;

    @Override
    @BeforeEach
    public void tearUp(Vertx vertx, VertxTestContext ctx) {
        super.tearUp(vertx, ctx);
        this.prepareDatabase(ctx, connOpt, "mysql_data/book_author.sql");
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
    void test_query_count(VertxTestContext ctx) {
        final Checkpoint cp = ctx.checkpoint(1);
        Assertions.assertEquals(8, _dsl.selectCount().from(Tables.AUTHORS).fetchOne().getValue(0));
        cp.flag();
    }

    @Test
    void test_fn_returns_value(VertxTestContext ctx) {
        final Checkpoint cp = ctx.checkpoint();
        final Hello routine = new Hello();
        routine.setS("hehe");
        JooqSQL.printJooqRoutine(_dsl, routine);
        routine.execute(_dsl.configuration());
        JooqSQL.printJooqRoutineResult(routine);
        cp.flag();
    }

    @Test
    void test_proc_returns_outParams(VertxTestContext ctx) {
        final Checkpoint cp = ctx.checkpoint();
        final CountAuthorByCountry proc = new CountAuthorByCountry();
        proc.setCountry("USA");
        JooqSQL.printJooqRoutine(_dsl, proc);
        proc.execute(_dsl.configuration());
        JooqSQL.printJooqRoutineResult(proc);
        cp.flag();
    }

    @Test
    void test_proc_return_void(VertxTestContext ctx) {
        final Checkpoint cp = ctx.checkpoint(1);

        final RemoveAuthor proc = new RemoveAuthor();
        proc.setAuthorName("J.D. Salinger");
        JooqSQL.printJooqRoutine(_dsl, proc);
        proc.execute(_dsl.configuration());
        JooqSQL.printJooqRoutineResult(proc);
        cp.flag();
    }

    @Test
    void test_proc_return_resultSet(VertxTestContext ctx) {
        final Checkpoint cp = ctx.checkpoint(1);

        final SelectBooksByAuthor proc = new SelectBooksByAuthor();
        proc.setAuthor("J.D. Salinger");
        JooqSQL.printJooqRoutine(_dsl, proc);
        proc.execute(_dsl.configuration());
        JooqSQL.printJooqRoutineResult(proc);
        cp.flag();
    }

}
