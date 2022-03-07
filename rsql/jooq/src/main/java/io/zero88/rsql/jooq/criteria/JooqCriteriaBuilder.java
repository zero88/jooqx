package io.zero88.rsql.jooq.criteria;

import org.jetbrains.annotations.NotNull;
import org.jooq.Condition;
import org.jooq.TableLike;

import io.zero88.rsql.criteria.CriteriaBuilder;
import io.zero88.rsql.jooq.JooqRSQLContext;

import cz.jirutka.rsql.parser.ast.Node;

/**
 * The interface Criteria builder.
 *
 * @param <N> Type of {@code Node}
 * @see Node
 * @since 1.0.0
 */
public interface JooqCriteriaBuilder<N extends Node> extends CriteriaBuilder<N, JooqRSQLContext, Condition> {

    /**
     * Build a condition.
     *
     * @param context the jOOQ RSQL context
     * @return the condition
     * @see Condition
     * @see JooqRSQLContext
     * @since 1.0.0
     */
    @NotNull Condition build(@NotNull JooqRSQLContext context);

    /**
     * Build condition.
     *
     * @param table the table
     * @return the condition
     * @apiNote It is equivalent to call {@link #build(JooqRSQLContext)} with {@link
     *     JooqRSQLContext#create(TableLike)}
     * @since 1.0.0
     */
    @SuppressWarnings("rawtypes")
    default @NotNull Condition build(@NotNull TableLike table) {
        return build(JooqRSQLContext.create(table));
    }

}
