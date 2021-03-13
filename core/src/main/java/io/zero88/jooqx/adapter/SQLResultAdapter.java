package io.zero88.jooqx.adapter;

import org.jooq.Record;
import org.jooq.TableLike;

import io.zero88.jooqx.SQLResultCollector;
import io.zero88.jooqx.datatype.SQLDataTypeRegistry;

import lombok.NonNull;

/**
 * SQL Result adapter receives Result set then mapping to expected result
 *
 * @param <RS> Type of Vertx Result set
 * @param <C>  Type of SQL result set collector
 * @param <T>  Type of jOOQ Table in Query context
 * @param <O>  Type of an expectation output
 * @see TableLike
 * @see Record
 * @see SQLResultCollector
 * @since 1.0.0
 */
public interface SQLResultAdapter<RS, C extends SQLResultCollector<RS>, T extends TableLike<? extends Record>, O>
    extends HasStrategy {

    /**
     * A current context holder
     *
     * @return jOOQ table
     * @see TableLike
     */
    @NonNull T table();

    /**
     * Declares Result set converter
     *
     * @return converter
     */
    @NonNull C converter();

    /**
     * Collect result set to expected result
     *
     * @param resultSet result set
     * @param registry  SQL data type mapper registry
     * @return result
     * @see SQLDataTypeRegistry
     */
    O collect(@NonNull RS resultSet, @NonNull SQLDataTypeRegistry registry);

    /**
     * Indicates select only one row
     *
     * @since 1.0.0
     */
    interface SelectOne extends HasStrategy {

        @Override
        default @NonNull SelectStrategy strategy() {
            return SelectStrategy.FIRST_ONE;
        }

    }


    /**
     * Indicates select many row
     *
     * @since 1.0.0
     */
    interface SelectMany extends HasStrategy {

        @Override
        default @NonNull SelectStrategy strategy() {
            return SelectStrategy.MANY;
        }

    }

}
