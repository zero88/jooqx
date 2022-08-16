package io.github.zero88.jooqx;

import java.util.List;
import java.util.stream.Collector;
import java.util.stream.StreamSupport;

import org.jetbrains.annotations.NotNull;

import io.vertx.codegen.annotations.VertxGen;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;

/**
 * Represents for a collector that collects {@code Vert.x SQL block result} to an expectation output
 *
 * @see JooqxResultCollector
 * @since 2.0.0
 */
@VertxGen
public interface JooqxBlockCollector {

    static JooqxBlockCollector create() { return new JooqxBlockCollector() { }; }

    /**
     * Collect to block result
     *
     * @param resultSet the block result set
     * @return block result
     * @see BlockResult
     * @see RowSet
     */
    default BlockResult blockResult(@NotNull RowSet<Row> resultSet, List<Collector<Row, List, Object>> collectors) {
        RowSet<Row> rs = resultSet;
        final int[] count = { 0 };
        final BlockResult result = BlockResult.create();
        do {
            result.append(StreamSupport.stream(rs.spliterator(), false).collect(collectors.get(count[0])));
            count[0]++;
        } while ((rs = rs.next()) != null);
        return result;
    }

}
