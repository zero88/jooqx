package io.zero88.rsql.criteria;

import org.jetbrains.annotations.NotNull;

import io.zero88.rsql.RSQLContext;

import cz.jirutka.rsql.parser.ast.Node;

public abstract class AbstractCriteriaBuilder<N extends Node, C extends RSQLContext, P>
    implements CriteriaBuilder<N, C, P> {

    protected N node;

    protected AbstractCriteriaBuilder() {}

    protected AbstractCriteriaBuilder(N node) {
        this.node = node;
    }

    public final @NotNull N node() {
        return node;
    }

}
