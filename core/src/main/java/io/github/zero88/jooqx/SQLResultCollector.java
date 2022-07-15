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
 * Represents for a collector that collects {@code Vert.x SQL result} to an expectation output
 *
 * @param <RS> Type of Vertx SQL result set
 * @see LegacySQLCollector
 * @see JooqxResultCollector
 * @see JooqxBatchCollector
 * @since 1.0.0
 */
public interface SQLResultCollector<RS> {

    @GenIgnore
    Logger LOGGER = LoggerFactory.getLogger(SQLResultCollector.class);

    @GenIgnore
    default void warnManyResult(boolean moreThanOneRow, @NotNull SelectStrategy strategy) {
        if (moreThanOneRow) {
            LOGGER.warn("Query strategy is [{}] but query result contains more than one row", strategy);
        }
    }

    /**
     * Collect result set to an expectation result that defines in SQL result adapter
     *
     * @param <ROW>     the type of jOOQ record of the reduction operation
     * @param <RESULT>  the result type of the reduction operation
     * @param resultSet result set
     * @return an expectation result
     * @see SQLResultAdapter
     */
    @Nullable <ROW, RESULT> RESULT collect(@NotNull RS resultSet, @NotNull SQLResultAdapter<ROW, RESULT> adapter,
                                           @NotNull DSLContext dslContext, @NotNull DataTypeMapperRegistry registry);

}
