package io.github.zero88.jooqx.adapter;

import java.util.function.Function;
import java.util.stream.Collector;

import org.jetbrains.annotations.NotNull;
import org.jooq.Field;

/**
 * A row converter strategy collects each row in result set to an expectation result
 *
 * @param <REC> Type of the jOOQ record
 * @param <R>   Type of the final result
 * @implNote {@code REC} must be subtype of {@link org.jooq.Record}, however, the Rxify/Mutiny Vertx generation does
 *     not work on the java method that has the bounded type parameter.
 */
public interface RowConverterStrategy<REC, R> extends HasStrategy {

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
    @NotNull Collector<Field<?>, REC, R> createCollector(@NotNull Function<Field<?>, Object> getValue);

}
