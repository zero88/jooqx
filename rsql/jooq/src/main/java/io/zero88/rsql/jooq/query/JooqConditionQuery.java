package io.zero88.rsql.jooq.query;

import org.jetbrains.annotations.NotNull;
import org.jooq.Condition;
import org.jooq.Query;
import org.jooq.TableLike;

/**
 * The interface Jooq condition query.
 *
 * @param <R> Type of {@code Result}
 * @see Condition
 * @see JooqRqlQuery
 * @since 1.0.0
 */
public interface JooqConditionQuery<R> extends JooqRqlQuery<R, Condition, Void> {

    /**
     * Defines table.
     *
     * @return the table
     * @see TableLike
     * @since 1.0.0
     */
    @NotNull TableLike table();

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
