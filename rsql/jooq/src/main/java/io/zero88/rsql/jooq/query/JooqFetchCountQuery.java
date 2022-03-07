package io.zero88.rsql.jooq.query;

import org.jetbrains.annotations.NotNull;
import org.jooq.Condition;
import org.jooq.Record1;
import org.jooq.Select;

/**
 * Represents for jOOQ fetch count query.
 *
 * @see JooqConditionQuery
 * @since 1.0.0
 */
public final class JooqFetchCountQuery extends AbstractJooqConditionQuery<Select<Record1<Integer>>, Integer> {

    @Override
    public @NotNull Integer execute(@NotNull Condition condition) {
        return toQuery(condition).fetchOptional().map(Record1::value1).orElse(0);
    }

    @Override
    public @NotNull Select<Record1<Integer>> toQuery(@NotNull Condition condition) {
        return context().dsl().selectCount().from(context().subject()).where(condition);
    }

    private JooqFetchCountQuery(JooqFetchCountQueryBuilder b) {
        super(b);
    }

    public static JooqFetchCountQueryBuilder builder() {return new JooqFetchCountQueryBuilder();}

    public static final class JooqFetchCountQueryBuilder extends
                                                         AbstractJooqConditionQueryBuilder<Select<Record1<Integer>>,
                                                                                              Integer,
                                                                                              JooqFetchCountQuery,
                                                                                              JooqFetchCountQueryBuilder> {

        protected JooqFetchCountQueryBuilder self() {return this;}

        public JooqFetchCountQuery build()          {return new JooqFetchCountQuery(this);}

    }

}
