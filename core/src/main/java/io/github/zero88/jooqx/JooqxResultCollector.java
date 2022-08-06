package io.github.zero88.jooqx;

import java.util.List;
import java.util.stream.Collector;

import org.jetbrains.annotations.NotNull;
import org.jooq.DSLContext;

import io.github.zero88.jooqx.JooqxSQLImpl.ReactiveSQLRC;
import io.github.zero88.jooqx.adapter.SQLResultAdapter;
import io.github.zero88.jooqx.datatype.DataTypeMapperRegistry;
import io.vertx.codegen.annotations.GenIgnore;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.sqlclient.Row;

/**
 * Represents for a collector that collects then transform {@code Vert.x SQL result} to an expectation output
 *
 * @since 2.0.0
 */
@VertxGen
public interface JooqxResultCollector extends SQLResultCollector {

    static JooqxResultCollector create() {
        return new ReactiveSQLRC();
    }

    /**
     * Create collector that helps transform result set to an expectation result that defines in SQL result adapter
     *
     * @param adapter    SQL result adapter
     * @param dslContext dsl context
     * @param registry   data type mapper registry
     * @param <ROW>      Type of intermediate row, might be jOOQ record or custom type
     * @param <RESULT>   Type of expectation result
     * @return the collector
     */
    @GenIgnore
    <ROW, RESULT> @NotNull Collector<Row, List<ROW>, RESULT> collector(@NotNull SQLResultAdapter<ROW, RESULT> adapter,
                                                                       @NotNull DSLContext dslContext,
                                                                       @NotNull DataTypeMapperRegistry registry);

}
