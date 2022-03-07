package io.zero88.rsql.criteria;

import org.jetbrains.annotations.NotNull;

import io.zero88.rsql.RSQLContext;

import cz.jirutka.rsql.parser.ast.Node;

/**
 * The builder builds a SQL predicate that can be used in a variety of SQL clauses, they're mainly used in a {@code
 * Select} statement's <code>WHERE</code> clause
 *
 * @param <N> Type of AST node
 * @param <C> Type of RSQL context
 * @param <P> Type of SQL predicate
 * @see Node
 * @see RSQLContext
 * @since 1.0.0
 */
public interface CriteriaBuilder<N extends Node, C extends RSQLContext, P> {

    /**
     * Represents for current AST node.
     *
     * @return the AST node
     * @see Node
     * @since 1.0.0
     */
    @NotNull N node();

    /**
     * Build a SQL predicate.
     *
     * @param context the rsql context
     * @return the condition
     * @since 1.0.0
     */
    @NotNull P build(@NotNull C context);

}
