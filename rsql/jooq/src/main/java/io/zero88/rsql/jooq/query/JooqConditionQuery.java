package io.zero88.rsql.jooq.query;

import org.jetbrains.annotations.NotNull;
import org.jooq.Condition;
import org.jooq.Query;

/**
 * The interface Jooq condition query.
 *
 * @param <R> Type of {@code Result}
 * @see Condition
 * @see JooqRSQLQuery
 * @since 1.0.0
 */
public interface JooqConditionQuery<R> extends JooqRSQLQuery<R> {

    /**
     * Execute query by condition.
     *
     * @param condition the condition
     * @return the result
     * @since 1.0.0
     */
    @NotNull R execute(@NotNull Condition condition);

    @NotNull Query toQuery(@NotNull Condition condition);

}
