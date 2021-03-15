package io.zero88.jooqx.adapter;

import java.util.function.Function;
import java.util.stream.Collector;

import org.jooq.Field;
import org.jooq.Record;

import lombok.NonNull;

/**
 * A row converter strategy collects each row in result set to an expectation result
 *
 * @param <R> Type of current record
 * @param <I> Type of output
 */
public interface RowConverterStrategy<R extends Record, I> extends HasStrategy {

    /**
     * Lookup jOOQ field in current jOOQ Query context
     *
     * @param fieldName field name
     * @return jOOQ field, it is nullable
     */
    Field<?> lookupField(@NonNull String fieldName);

    /**
     * Create collector to accumulate each SQL Vert.x row to an expectation output
     *
     * @param getValue a function to collect field value by field
     * @return a collector
     */
    @NonNull Collector<Field<?>, R, I> createCollector(@NonNull Function<Field<?>, Object> getValue);

}
