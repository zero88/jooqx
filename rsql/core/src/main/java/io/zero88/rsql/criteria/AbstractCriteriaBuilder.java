package io.zero88.rsql.criteria;

import org.jetbrains.annotations.NotNull;

import cz.jirutka.rsql.parser.ast.Node;

public abstract class AbstractCriteriaBuilder<T extends Node> implements CriteriaBuilder<T> {

    protected T node;

    protected AbstractCriteriaBuilder() {}

    protected AbstractCriteriaBuilder(T node) {
        this.node = node;
    }

    public final @NotNull T node() {
        return node;
    }

}
