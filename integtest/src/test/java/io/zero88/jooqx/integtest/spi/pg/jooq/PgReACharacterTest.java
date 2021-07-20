package io.zero88.jooqx.integtest.spi.pg.jooq;

import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.vertx.core.Vertx;
import io.vertx.junit5.Checkpoint;
import io.vertx.junit5.VertxTestContext;
import io.vertx.pgclient.PgPool;
import io.zero88.jooqx.DSLAdapter;
import io.zero88.jooqx.integtest.pgsql.tables.CharacterDataType;
import io.zero88.jooqx.integtest.pgsql.tables.records.CharacterDataTypeRecord;
import io.zero88.jooqx.integtest.spi.pg.PostgreSQLHelper.PgUseJooqType;
import io.zero88.jooqx.spi.pg.PgPoolProvider;
import io.zero88.jooqx.spi.pg.PgSQLReactiveTest;
import io.zero88.jooqx.spi.pg.PgSQLErrorConverterProvider;

class PgReACharacterTest extends PgSQLReactiveTest<PgPool>
    implements PgSQLErrorConverterProvider, PgPoolProvider, PgUseJooqType {

    @Override
    @BeforeEach
    public void tearUp(Vertx vertx, VertxTestContext ctx) {
        super.tearUp(vertx, ctx);
        this.prepareDatabase(ctx, this, connOpt, "pg_data/character.sql");
    }

    @Test
    void queryCharacter(VertxTestContext ctx) {
        final Checkpoint flag = ctx.checkpoint();
        final CharacterDataType table = schema().CHARACTER_DATA_TYPE;
        jooqx.execute(jooqx.dsl().selectFrom(table).limit(1), DSLAdapter.fetchOne(table), ar -> ctx.verify(() -> {
            final CharacterDataTypeRecord record = assertSuccess(ctx, ar);
            System.out.println(record);
            Assertions.assertNotNull(record.getName());

            Assertions.assertNotNull(record.getFixedchar());
            Assertions.assertNotNull(record.getSinglechar());

            Assertions.assertNotNull(record.getText());
            Assertions.assertNotNull(record.getVarcharacter());

            Assertions.assertNotNull(record.getUuid());
            Assertions.assertNotNull(record.getBytea());
            Assertions.assertEquals("HELLO", new String(record.getBytea(), StandardCharsets.UTF_8));
            flag.flag();
        }));
    }

}
