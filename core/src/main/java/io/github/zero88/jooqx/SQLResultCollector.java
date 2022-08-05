package io.github.zero88.jooqx;

import org.jetbrains.annotations.NotNull;
import org.jooq.DSLContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.zero88.jooqx.adapter.SQLResultAdapter;
import io.github.zero88.jooqx.adapter.SelectStrategy;
import io.github.zero88.jooqx.datatype.DataTypeMapperRegistry;
import io.vertx.codegen.annotations.GenIgnore;
import io.vertx.codegen.annotations.Nullable;

/**
 * Represents for a collector that collects and transforms {@code Vert.x SQL result} to an expectation output
 *
 * @param <RS> Type of Vertx SQL result set
 * @see LegacySQLCollector
 * @see JooqxResultCollector
 * @see JooqxBatchCollector
 * @since 1.0.0
 */
public interface SQLResultCollector<RS> {

    /**
     * Collect result set to an expectation result that defines in SQL result adapter
     *
     * @param <ROW>     the type of jOOQ record of the reduction operation
     * @param <RESULT>  the type of result after the reduction operation
     * @param resultSet result set
     * @return an expectation result
     * @see SQLResultAdapter
     * @see DataTypeMapperRegistry
     * @since 2.0.0
     */
    @Nullable <ROW, RESULT> RESULT collect(@NotNull RS resultSet, @NotNull SQLResultAdapter<ROW, RESULT> adapter,
                                           @NotNull DSLContext dslContext, @NotNull DataTypeMapperRegistry registry);

}
