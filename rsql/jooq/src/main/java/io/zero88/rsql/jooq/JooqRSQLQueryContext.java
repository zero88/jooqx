package io.zero88.rsql.jooq;

import org.jetbrains.annotations.NotNull;
import org.jooq.DSLContext;
import org.jooq.TableLike;

import io.zero88.rsql.jooq.criteria.JooqCriteriaBuilderFactory;

public interface JooqRSQLQueryContext extends JooqRSQLContext {

    /**
     * Defines dsl context.
     *
     * @return the dsl context
     * @since 1.0.0
     */
    @NotNull DSLContext dsl();

    @SuppressWarnings("rawtypes")
    static JooqRSQLQueryContext create(DSLContext dslContext, TableLike table) {
        final JooqRSQLContext context = JooqRSQLContext.create(table);
        return new JooqRSQLQueryContext() {
            @Override
            public @NotNull DSLContext dsl() {
                return dslContext;
            }

            @Override
            public @NotNull TableLike subject() {
                return context.subject();
            }

            @Override
            public @NotNull JooqQueryContext queryContext() {
                return context.queryContext();
            }

            @Override
            public @NotNull JooqCriteriaBuilderFactory criteriaBuilderFactory() {
                return context.criteriaBuilderFactory();
            }
        };
    }

}
