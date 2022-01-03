package io.zero88.rsql.jooq.visitor;

import org.jooq.DSLContext;

import io.zero88.rsql.jooq.AbstractJooqRqlFacade;

import cz.jirutka.rsql.parser.ast.AndNode;
import cz.jirutka.rsql.parser.ast.ComparisonNode;
import cz.jirutka.rsql.parser.ast.Node;
import cz.jirutka.rsql.parser.ast.OrNode;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

/**
 * Represents for Abstract jOOQ DSL RQL visitor.
 *
 * @param <R> Type of {@code Result}
 * @see JooqDSLRqlVisitor
 * @since 1.0.0
 */
@Getter
@SuperBuilder
@Accessors(fluent = true)
public abstract class AbstractJooqDSLVisitor<R> extends AbstractJooqRqlFacade implements JooqDSLRqlVisitor<R> {

    @Override
    public R visit(AndNode node, DSLContext dsl) {
        return build(node, dsl);
    }

    @Override
    public R visit(OrNode node, DSLContext dsl) {
        return build(node, dsl);
    }

    @Override
    public R visit(ComparisonNode node, DSLContext dsl) {
        return build(node, dsl);
    }

    @NonNull
    protected abstract R build(@NonNull Node node, DSLContext dsl);

}
