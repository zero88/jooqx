package io.zero88.rsql.jooq.query;

import org.jooq.Condition;
import org.jooq.Record1;
import org.jooq.Select;
import org.jooq.impl.DSL;

import lombok.NonNull;
import lombok.experimental.SuperBuilder;

/**
 * Represents for jOOQ fetch exist query.
 *
 * @see JooqConditionQuery
 * @since 1.0.0
 */
@SuperBuilder
public final class JooqFetchExistQuery extends AbstractJooqConditionQuery<Boolean> {

    @Override
    public Boolean execute(@NonNull Condition condition) {
        return toQuery(condition).fetchOne() != null;
    }

    @Override
    public @NonNull Select<Record1<Integer>> toQuery(@NonNull Condition condition) {
        return dsl().selectOne().whereExists(dsl().select(DSL.asterisk()).from(table()).where(condition));
    }

}
