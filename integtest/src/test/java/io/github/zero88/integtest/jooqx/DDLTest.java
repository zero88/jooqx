package io.github.zero88.integtest.jooqx;

import org.jooq.Record1;
import org.jooq.SelectJoinStep;
import org.jooq.Table;
import org.junit.jupiter.api.Assertions;

import io.github.zero88.jooqx.DSLAdapter;
import io.github.zero88.jooqx.SQLExecutor;
import io.vertx.junit5.Checkpoint;
import io.vertx.junit5.VertxTestContext;

public interface DDLTest {

    default void createTableThenAssert(VertxTestContext testContext, SQLExecutor jooqx, Table table) {
        Checkpoint flag = testContext.checkpoint();
        final SelectJoinStep<Record1<Integer>> query = jooqx.dsl().selectCount().from(table);
        jooqx.ddl(dsl -> dsl.createSchemaIfNotExists(table.getSchema()))
             .onSuccess(i -> System.out.println("=== Create schema [" + i + "]============"))
             .flatMap(i -> jooqx.ddl(dsl -> dsl.createTableIfNotExists(table).columns(table.fields())))
             .onSuccess(i -> System.out.println("=== Create table [" + table.getName() + "] [" + i + "]============"))
             .flatMap(ignore -> jooqx.execute(query, DSLAdapter.fetchCount(query.asTable())))
             .onSuccess(count -> testContext.verify(() -> {
                 Assertions.assertEquals(0, count);
                 flag.flag();
             }))
             .onFailure(testContext::failNow);
    }

}
