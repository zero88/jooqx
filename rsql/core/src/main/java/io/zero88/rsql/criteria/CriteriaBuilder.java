package io.zero88.rsql.criteria;

import org.jetbrains.annotations.NotNull;

import cz.jirutka.rsql.parser.ast.Node;

public interface CriteriaBuilder<N extends Node> {

    /**
     * Represents for current AST node.
     *
     * @return the AST node
     * @see Node
     * @since 1.0.0
     */
    @NotNull N node();

}
