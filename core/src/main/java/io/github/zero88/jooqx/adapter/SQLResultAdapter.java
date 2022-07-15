package io.github.zero88.jooqx.adapter;

import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jooq.Record;

/**
 * SQL Result adapter receives result set then mapping to expected result
 *
 * @param <ROW>    Type of SQL row
 * @param <RESULT> Type of expectation result
 * @since 1.0.0
 */
public interface SQLResultAdapter<ROW, RESULT> extends HasStrategy {

    /**
     * @return record factory
     * @see RecordFactory
     */
    RecordFactory<? extends Record, ROW> recordFactory();

    /**
     * Collect list of row to an expectation result
     *
     * @param records row list
     * @return an expectation result
     */
    RESULT collect(@NotNull List<ROW> records);

}
