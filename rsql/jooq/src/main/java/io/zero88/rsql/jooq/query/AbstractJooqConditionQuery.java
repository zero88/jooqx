package io.zero88.rsql.jooq.query;

import org.jetbrains.annotations.NotNull;
import org.jooq.Query;

import io.zero88.rsql.jooq.JooqRSQLParser;
import io.zero88.rsql.jooq.JooqRSQLQueryContext;

public abstract class AbstractJooqConditionQuery<Q extends Query, R> extends AbstractJooqQuery<Q, R>
    implements JooqConditionQuery<Q, R> {

    protected AbstractJooqConditionQuery(AbstractJooqConditionQueryBuilder<Q, R, ?, ?> b) {
        super(b.parser, b.context);
    }

    public @NotNull R execute(@NotNull String query) {
        return execute(parser().criteria(query, context()));
    }

    @Override
    public @NotNull Q toQuery(@NotNull String query) {
        return toQuery(parser().criteria(query, context()));
    }

    public abstract static class AbstractJooqConditionQueryBuilder<Q extends Query, R,
                                                                      C extends AbstractJooqConditionQuery<Q, R>,
                                                                      B extends AbstractJooqConditionQueryBuilder<Q, R, C, B>> {

        private JooqRSQLParser parser;
        private JooqRSQLQueryContext context;

        public B parser(JooqRSQLParser parser) {
            this.parser = parser;
            return self();
        }

        public B context(JooqRSQLQueryContext context) {
            this.context = context;
            return self();
        }

        protected abstract B self();

        public abstract C build();

    }

}
