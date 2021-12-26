package io.zero88.rsql.jooq;

import io.zero88.rsql.RqlVisitor;
import io.zero88.rsql.jooq.criteria.JooqCriteriaBuilderFactory;

import cz.jirutka.rsql.parser.ast.RSQLVisitor;
import lombok.NonNull;

/**
 * The interface jOOQ RQL visitor.
 *
 * @param <R> Type of {@code Visitor Result}
 * @param <C> Type of {@code Visitor Context}
 * @see RSQLVisitor
 * @since 1.0.0
 */
public interface JooqRqlVisitor<R, C> extends RqlVisitor<R, C>, JooqRqlFacade {

    default @NonNull JooqCriteriaBuilderFactory criteriaBuilderFactory() {
        return JooqRqlFacade.super.criteriaBuilderFactory();
    }

}
