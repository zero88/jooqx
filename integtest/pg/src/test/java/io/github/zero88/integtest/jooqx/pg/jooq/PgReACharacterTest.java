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
import io.github.zero88.sample.model.pgsql.tables.AllDataTypes;
import io.github.zero88.sample.model.pgsql.tables.records.AllDataTypesRecord;
import io.vertx.core.Vertx;
import io.vertx.junit5.Checkpoint;
import io.vertx.junit5.VertxTestContext;
import io.vertx.pgclient.PgPool;

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
        final AllDataTypes table = schema().ALL_DATA_TYPES;
        jooqx.execute(dsl -> dsl.selectFrom(table).where(table.ID.eq(21)).limit(1), DSLAdapter.fetchOne(table),
                      ar -> ctx.verify(() -> {
                          final AllDataTypesRecord record = assertSuccess(ctx, ar);
                          System.out.println(record);

                          Assertions.assertNotNull(record.getFStrFixedChar());
                          Assertions.assertNotNull(record.getFStrChar());

                          Assertions.assertNotNull(record.getFStrText());
                          Assertions.assertNotNull(record.getFStrVarchar());

                          Assertions.assertNotNull(record.getFMiscName());
                          Assertions.assertNotNull(record.getFMiscUuid());
                          Assertions.assertNotNull(record.getFMiscBytea());
                          Assertions.assertEquals("HELLO", new String(record.getFMiscBytea(), StandardCharsets.UTF_8));
                          flag.flag();
                      }));
    }

}
