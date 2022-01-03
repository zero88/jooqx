package io.zero88.rsql.jooq.visitor;

import org.jooq.Condition;
import org.jooq.TableLike;

import io.zero88.rsql.jooq.JooqQueryContext;
import io.zero88.rsql.jooq.criteria.JooqCriteriaBuilderFactory;

import lombok.NonNull;

/**
 * The interface jOOQ condition RQL visitor.
 *
 * @see Condition
 * @since 1.0.0
 */
public interface JooqConditionRqlVisitor extends JooqNoArgsRqlVisitor<Condition> {

    /**
     * Create jooq condition rql visitor.
     *
     * @param table the table
     * @return the jooq condition rql visitor
     * @see TableLike
     * @since 1.0.0
     */
    static JooqConditionRqlVisitor create(@NonNull TableLike table) {
        return DefaultJooqConditionRqlVisitor.builder().table(table).build();
    }

    /**
     * Create jooq condition rql visitor.
     *
     * @param table        the table
     * @param queryContext the query context
     * @return the jooq condition rql visitor
     * @see TableLike
     * @see JooqQueryContext
     * @since 1.0.0
     */
    static JooqConditionRqlVisitor create(@NonNull TableLike table, JooqQueryContext queryContext) {
        return DefaultJooqConditionRqlVisitor.builder().table(table).queryContext(queryContext).build();
    }

    /**
     * Create jooq condition rql visitor.
     *
     * @param table        the table
     * @param queryContext the query context
     * @param factory      the factory
     * @return the jooq condition rql visitor
     * @see TableLike
     * @see JooqQueryContext
     * @see JooqCriteriaBuilderFactory
     * @since 1.0.0
     */
    static JooqConditionRqlVisitor create(@NonNull TableLike table, JooqQueryContext queryContext,
                                          JooqCriteriaBuilderFactory factory) {
        return DefaultJooqConditionRqlVisitor.builder()
                                             .table(table)
                                             .queryContext(queryContext)
                                             .criteriaBuilderFactory(factory)
                                             .build();
    }

}
