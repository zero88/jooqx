package io.zero88.rsql.jooq.visitor;

import io.zero88.rsql.jooq.JooqRqlVisitor;

import cz.jirutka.rsql.parser.ast.AndNode;
import cz.jirutka.rsql.parser.ast.ComparisonNode;
import cz.jirutka.rsql.parser.ast.OrNode;

/**
 * The interface jOOQ RQL visitor with no arguments.
 *
 * @param <R> Type of {@code Result}
 * @since 1.0.0
 */
public interface JooqNoArgsRqlVisitor<R> extends JooqRqlVisitor<R, Void> {

    /**
     * Visit And Node.
     *
     * @param node the node
     * @return the result
     * @see AndNode
     * @since 1.0.0
     */
    R visit(AndNode node);

    /**
     * Visit Or Node.
     *
     * @param node the node
     * @return the result
     * @see OrNode
     * @since 1.0.0
     */
    R visit(OrNode node);

    /**
     * Visit Comparison Node.
     *
     * @param node the node
     * @return the result
     * @see ComparisonNode
     * @since 1.0.0
     */
    R visit(ComparisonNode node);

    default R visit(AndNode node, Void param) {
        return visit(node);
    }

    default R visit(OrNode node, Void param) {
        return visit(node);
    }

    default R visit(ComparisonNode node, Void param) {
        return visit(node);
    }

}
