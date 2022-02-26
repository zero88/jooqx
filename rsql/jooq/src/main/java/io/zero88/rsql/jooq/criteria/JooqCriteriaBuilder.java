package io.zero88.rsql.jooq.criteria;

import org.jetbrains.annotations.NotNull;
import org.jooq.Condition;
import org.jooq.Table;
import org.jooq.TableLike;

import io.zero88.rsql.criteria.CriteriaBuilder;
import io.zero88.rsql.jooq.JooqQueryContext;

import cz.jirutka.rsql.parser.ast.Node;

/**
 * The interface Criteria builder.
 *
 * @param <T> Type of {@code Node}
 * @see Node
 * @since 1.0.0
 */
public interface JooqCriteriaBuilder<T extends Node> extends CriteriaBuilder<T> {

    /**
     * Build condition.
     *
     * @param table the table
     * @return the condition
     * @apiNote It is equivalent to call {@link #build(TableLike, JooqQueryContext, JooqCriteriaBuilderFactory)} with
     *     {@link JooqQueryContext#DEFAULT} and {@link JooqCriteriaBuilderFactory#DEFAULT}
     * @since 1.0.0
     */
    default @NotNull Condition build(@NotNull TableLike table) {
        return build(table, JooqQueryContext.DEFAULT, JooqCriteriaBuilderFactory.DEFAULT);
    }

    /**
     * Build condition.
     *
     * @param table        the table
     * @param queryContext the query context
     * @param factory      the criteria builder factory
     * @return the condition
     * @see Condition
     * @see Table
     * @see JooqQueryContext
     * @see JooqCriteriaBuilderFactory
     * @since 1.0.0
     */
    @NotNull Condition build(@NotNull TableLike table, @NotNull JooqQueryContext queryContext,
                             @NotNull JooqCriteriaBuilderFactory factory);

}
