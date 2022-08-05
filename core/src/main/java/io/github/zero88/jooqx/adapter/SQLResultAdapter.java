package io.github.zero88.jooqx.adapter;

import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jooq.Record;

/**
 * Provides a capability to transform {@code Vert.x} SQL result set to an expected result (in {@code jOOQ} term).
 *
 * @param <ROW>    Type of SQL row. Might be jOOQ record or custom type
 * @param <RESULT> Type of expectation result
 * @apiNote Breaking changes from v1.0.0
 * @since 2.0.0
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
     * @param rows row list
     * @return an expectation result
     */
    RESULT collect(@NotNull List<ROW> rows);

}
