package io.zero88.rsql.criteria;

import cz.jirutka.rsql.parser.ast.Node;
import lombok.NonNull;

public interface CriteriaBuilder<T extends Node> {

    /**
     * Represents for current AST node.
     *
     * @return the AST node
     * @see Node
     * @since 1.0.0
     */
    @NonNull T node();

}
