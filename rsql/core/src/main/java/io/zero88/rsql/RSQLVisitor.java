package io.zero88.rsql;

import cz.jirutka.rsql.parser.ast.AndNode;
import cz.jirutka.rsql.parser.ast.ComparisonNode;
import cz.jirutka.rsql.parser.ast.OrNode;

/**
 * The visitor interface for visiting AST nodes of the RSQL.
 *
 * @param <R> Type of {@code Visitor Result}
 * @param <C> Type of {@link RSQLContext}
 * @see cz.jirutka.rsql.parser.ast.RSQLVisitor
 * @since 1.0.0
 */
public interface RSQLVisitor<R, C extends RSQLContext> extends cz.jirutka.rsql.parser.ast.RSQLVisitor<R, C> {

    @Override
    R visit(AndNode node, C context);

    @Override
    R visit(OrNode node, C context);

    @Override
    R visit(ComparisonNode node, C context);

}
