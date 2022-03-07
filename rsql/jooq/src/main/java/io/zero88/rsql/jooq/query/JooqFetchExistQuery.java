package io.zero88.rsql.jooq.query;

import org.jetbrains.annotations.NotNull;
import org.jooq.Condition;
import org.jooq.Record1;
import org.jooq.Select;
import org.jooq.impl.DSL;

/**
 * Represents for jOOQ fetch exist query.
 *
 * @see JooqConditionQuery
 * @since 1.0.0
 */
public final class JooqFetchExistQuery extends AbstractJooqConditionQuery<Select<Record1<Integer>>, Boolean> {

    @Override
    public @NotNull Boolean execute(@NotNull Condition condition) {
        return toQuery(condition).fetchOne() != null;
    }

    @Override
    public @NotNull Select<Record1<Integer>> toQuery(@NotNull Condition condition) {
        return context().dsl()
                        .selectOne()
                        .whereExists(context().dsl().select(DSL.asterisk()).from(context().subject()).where(condition));
    }

    private JooqFetchExistQuery(JooqFetchExistQueryBuilder b) {
        super(b);
    }

    public static JooqFetchExistQueryBuilder builder() {return new JooqFetchExistQueryBuilder();}

    public static final class JooqFetchExistQueryBuilder extends
                                                         AbstractJooqConditionQueryBuilder<Select<Record1<Integer>>,
                                                                                              Boolean,
                                                                                              JooqFetchExistQuery,
                                                                                              JooqFetchExistQueryBuilder> {

        protected JooqFetchExistQueryBuilder self() {return this;}

        public JooqFetchExistQuery build()          {return new JooqFetchExistQuery(this);}

    }

}
