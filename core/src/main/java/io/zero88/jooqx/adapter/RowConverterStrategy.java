package io.zero88.jooqx.adapter;

import java.util.function.Function;
import java.util.stream.Collector;

import org.jetbrains.annotations.NotNull;
import org.jooq.Field;

/**
 * A row converter strategy collects each row in result set to an expectation result
 *
 * @param <R> Type of current record
 * @param <I> Type of output
 */
public interface RowConverterStrategy<R, I> extends HasStrategy {

    /**
     * Lookup jOOQ field in current jOOQ Query context
     *
     * @param fieldName field name
     * @return jOOQ field, it is nullable
     */
    Field<?> lookupField(@NotNull String fieldName);

    /**
     * Create collector to accumulate each SQL Vert.x row to an expectation output
     *
     * @param getValue a function to collect field value by field
     * @return a collector
     */
    @NotNull Collector<Field<?>, R, I> createCollector(@NotNull Function<Field<?>, Object> getValue);

}
