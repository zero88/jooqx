package io.zero88.rsql.criteria;

import org.jetbrains.annotations.NotNull;

import cz.jirutka.rsql.parser.ast.Node;

public abstract class AbstractCriteriaBuilder<N extends Node> implements CriteriaBuilder<N> {

    protected N node;

    protected AbstractCriteriaBuilder() {}

    protected AbstractCriteriaBuilder(N node) {
        this.node = node;
    }

    public final @NotNull N node() {
        return node;
    }

}
