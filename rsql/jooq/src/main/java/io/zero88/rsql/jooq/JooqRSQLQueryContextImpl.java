package io.zero88.rsql.jooq;

import java.util.Objects;

import org.jetbrains.annotations.NotNull;
import org.jooq.DSLContext;
import org.jooq.TableLike;

import io.zero88.rsql.jooq.criteria.JooqCriteriaBuilderFactory;

class JooqRSQLQueryContextImpl implements JooqRSQLQueryContext {

    private final DSLContext dsl;
    private final JooqRSQLContext rsqlContext;

    JooqRSQLQueryContextImpl(DSLContext dsl, JooqRSQLContext rsqlContext) {
        this.dsl         = Objects.requireNonNull(dsl, "Required DSL context");
        this.rsqlContext = Objects.requireNonNull(rsqlContext, "Required jOOQ RSQL context");
    }

    @Override
    public @NotNull DSLContext dsl() {
        return dsl;
    }

    @Override
    @SuppressWarnings("rawtypes")
    public @NotNull TableLike subject() {
        return rsqlContext.subject();
    }

    @Override
    public @NotNull JooqQueryContext queryContext() {
        return rsqlContext.queryContext();
    }

    @Override
    public @NotNull JooqCriteriaBuilderFactory criteriaBuilderFactory() {
        return rsqlContext.criteriaBuilderFactory();
    }

}
