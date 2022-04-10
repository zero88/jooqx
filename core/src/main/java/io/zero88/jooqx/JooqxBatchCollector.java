package io.zero88.jooqx;

import org.jetbrains.annotations.NotNull;

import io.vertx.codegen.annotations.VertxGen;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;

/**
 * Represents for a collector that collects {@code Vert.x SQL batch result} to an expectation output
 *
 * @see JooqxResultCollector
 * @since 2.0.0
 */
@VertxGen
public interface JooqxBatchCollector extends JooqxResultCollector, SQLBatchCollector<RowSet<Row>, RowSet<Row>> {

    @Override
    int batchResultSize(@NotNull RowSet<Row> batchResult);

}
