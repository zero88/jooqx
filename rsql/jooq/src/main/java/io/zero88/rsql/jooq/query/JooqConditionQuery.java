package io.zero88.rsql.jooq.query;

import org.jetbrains.annotations.NotNull;
import org.jooq.Condition;
import org.jooq.Query;

/**
 * The interface Jooq condition query.
 *
 * @param <Q> Type of {@link Query}
 * @param <R> Type of {@code Result}
 * @see Condition
 * @see JooqRSQLQuery
 * @since 1.0.0
 */
public interface JooqConditionQuery<Q extends Query, R> extends JooqRSQLQuery<Q, R> {

    /**
     * Converts to jOOQ query.
     *
     * @param condition the condition
     * @return the jOOQ query
     * @see Query
     */
    @NotNull Q toQuery(@NotNull Condition condition);

    /**
     * Execute query by condition.
     *
     * @param condition the condition
     * @return the query result
     */
    @NotNull R execute(@NotNull Condition condition);

}
