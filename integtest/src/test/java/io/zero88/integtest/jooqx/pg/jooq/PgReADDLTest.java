package io.zero88.integtest.jooqx.pg.jooq;

import org.junit.jupiter.api.Test;

import io.vertx.junit5.VertxTestContext;
import io.vertx.pgclient.PgPool;
import io.zero88.integtest.jooqx.pg.PostgreSQLHelper.PgUseJooqType;
import io.zero88.integtest.jooqx.DDLTest;
import io.zero88.jooqx.spi.pg.PgPoolProvider;
import io.zero88.jooqx.spi.pg.PgSQLErrorConverterProvider;
import io.zero88.jooqx.spi.pg.PgSQLJooqxTest;

public class PgReADDLTest extends PgSQLJooqxTest<PgPool>
    implements PgSQLErrorConverterProvider, PgPoolProvider, PgUseJooqType, DDLTest {

    @Test
    void test_create_table(VertxTestContext testContext) {
        createTableThenAssert(testContext, jooqx, schema().BOOKS);
    }

}
