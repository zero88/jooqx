package io.zero88.rsql.jooq;

import org.jooq.DSLContext;
import org.jooq.Query;

import io.zero88.rsql.HasLog;
import io.zero88.rsql.jooq.criteria.JooqCriteriaBuilderFactory;
import io.zero88.rsql.jooq.visitor.JooqDSLRqlVisitor;

import lombok.NonNull;

/**
 * The interface Jooq rql query.
 *
 * @param <R> Type of {@code Query Result}
 * @param <T> Type of {@code Visitor result}
 * @param <C> Type of {@code Visitor context}
 * @since 1.0.0
 */
public interface JooqRqlQuery<R, T, C> extends JooqRqlFacade, HasLog {

    /**
     * Defines dsl context.
     *
     * @return the dsl context
     * @since 1.0.0
     */
    @NonNull DSLContext dsl();

    /**
     * Defines Jooq RQL Parser.
     *
     * @return the Jooq rql parser
     * @see JooqRqlParser
     * @since 1.0.0
     */
    @NonNull JooqRqlParser parser();

    /**
     * Defines jOOQ RQL visitor.
     *
     * @return the Jooq rql visitor
     * @see JooqDSLRqlVisitor
     * @since 1.0.0
     */
    @NonNull JooqRqlVisitor<T, C> visitor();

    /**
     * Execute.
     *
     * @param query the query in RQL
     * @return the result
     * @since 1.0.0
     */
    @NonNull R execute(@NonNull String query);

    /**
     * To jOOQ query
     *
     * @param query the query in RQL
     * @return jOOQ query
     */
    @NonNull Query toQuery(@NonNull String query);

    default @NonNull JooqCriteriaBuilderFactory criteriaBuilderFactory() {
        return JooqRqlFacade.super.criteriaBuilderFactory();
    }

}
