package io.zero88.jooqx.integtest.jdbc;

import java.util.Arrays;

import org.jooq.BatchBindStep;
import org.jooq.DSLContext;
import org.jooq.Result;
import org.jooq.impl.DSL;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;

import io.zero88.jooqx.LegacySQLTest.LegacyDBContainerTest;
import io.zero88.jooqx.integtest.PostgreSQLHelper;
import io.zero88.jooqx.integtest.pgsql.tables.Authors;
import io.zero88.jooqx.integtest.pgsql.tables.records.AuthorsRecord;
import io.zero88.jooqx.spi.PostgreSQLLegacyTest;
import io.vertx.core.Vertx;
import io.vertx.junit5.Checkpoint;
import io.vertx.junit5.VertxTestContext;

class PgSQLjOOQTest extends LegacyDBContainerTest<PostgreSQLContainer<?>>
    implements PostgreSQLLegacyTest, PostgreSQLHelper {

    @BeforeEach
    @Override
    public void tearUp(Vertx vertx, VertxTestContext ctx) {
        super.tearUp(vertx, ctx);
        this.prepareDatabase(ctx, this, connOpt);
    }

    @Test
    void test_batch_insert(VertxTestContext ctx) {
        final Checkpoint flag = ctx.checkpoint(2);
        final Authors table = catalog().PUBLIC.AUTHORS;
        final DSLContext dsl = dsl(createDataSource(connOpt));
        final BatchBindStep bind = dsl.batch(dsl.insertInto(table, table.NAME, table.COUNTRY, table.ID)
                                                .values(Arrays.asList(null, null, DSL.defaultValue())))
                                      .bind("Erich", "Gamma", 10)
                                      .bind("Richard", "Helm", 20)
                                      .bind("Ralph", "Johnson", 30)
                                      .bind("John", "Vlissides", 40);
        bind.execute();
        flag.flag();
        final Result<AuthorsRecord> fetch = dsl.selectFrom(table).fetch();
        fetch.forEach(System.out::println);
        flag.flag();
    }

}
