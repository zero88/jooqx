package io.github.zero88.integtest.jooqx.pg.jooq;

import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.github.zero88.integtest.jooqx.pg.PostgreSQLHelper.PgUseJooqType;
import io.github.zero88.jooqx.DSLAdapter;
import io.github.zero88.jooqx.spi.pg.PgPoolProvider;
import io.github.zero88.jooqx.spi.pg.PgSQLErrorConverterProvider;
import io.github.zero88.jooqx.spi.pg.PgSQLJooqxTest;
import io.vertx.core.Vertx;
import io.vertx.junit5.Checkpoint;
import io.vertx.junit5.VertxTestContext;
import io.vertx.pgclient.PgPool;
import io.zero88.sample.data.pgsql.tables.CharacterDataType;
import io.zero88.sample.data.pgsql.tables.records.CharacterDataTypeRecord;

class PgReACharacterTest extends PgSQLJooqxTest<PgPool>
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
