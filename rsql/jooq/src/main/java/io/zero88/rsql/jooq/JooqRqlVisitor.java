package io.zero88.rsql.jooq;

import org.jetbrains.annotations.NotNull;

import io.zero88.rsql.RqlVisitor;
import io.zero88.rsql.jooq.criteria.JooqCriteriaBuilderFactory;

import cz.jirutka.rsql.parser.ast.RSQLVisitor;

/**
 * The interface jOOQ RQL visitor.
 *
 * @param <R> Type of {@code Visitor Result}
 * @param <C> Type of {@code Visitor Context}
 * @see RSQLVisitor
 * @since 1.0.0
 */
public interface JooqRqlVisitor<R, C> extends RqlVisitor<R, C>, JooqRqlFacade {

    default @NotNull JooqCriteriaBuilderFactory criteriaBuilderFactory() {
        return JooqRqlFacade.super.criteriaBuilderFactory();
    }

}
