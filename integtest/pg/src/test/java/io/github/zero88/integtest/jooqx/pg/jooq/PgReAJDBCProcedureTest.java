package io.github.zero88.integtest.jooqx.pg.jooq;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.github.zero88.integtest.jooqx.pg.PgUseJooqType;
import io.github.zero88.jooqx.spi.jdbc.JDBCErrorConverterProvider;
import io.github.zero88.jooqx.spi.jdbc.JDBCPoolHikariProvider;
import io.github.zero88.jooqx.spi.pg.PgSQLJooqxTest;
import io.github.zero88.sample.model.pgsql.routines.Add;
import io.vertx.core.Vertx;
import io.vertx.jdbcclient.JDBCPool;
import io.vertx.junit5.Checkpoint;
import io.vertx.junit5.VertxTestContext;

class PgReAJDBCProcedureTest extends PgSQLJooqxTest<JDBCPool>
    implements PgUseJooqType, JDBCPoolHikariProvider, JDBCErrorConverterProvider {

    @Override
    @BeforeEach
    public void tearUp(Vertx vertx, VertxTestContext ctx) {
        super.tearUp(vertx, ctx);
        this.prepareDatabase(ctx, this, connOpt, "pg_data/numeric.sql");
    }

    @Test
    void executeProcedure(VertxTestContext ctx) {
        Checkpoint cp = ctx.checkpoint();
        final Add add = new Add();
        add.set__1(1);
        add.set__2(2);
        System.out.println(add.asField());
        System.out.println(add.getInParameters());
        System.out.println(add.getOutParameters());

        jooqx.routine(add).onSuccess(output -> ctx.verify(() -> {
            Assertions.assertEquals(3, output);
            cp.flag();
        })).onFailure(ctx::failNow);
    }

}
