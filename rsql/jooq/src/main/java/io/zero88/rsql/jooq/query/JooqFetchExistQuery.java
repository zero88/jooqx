package io.zero88.rsql.jooq.query;

import org.jetbrains.annotations.NotNull;
import org.jooq.Condition;
import org.jooq.Record1;
import org.jooq.Select;
import org.jooq.impl.DSL;

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
    public @NotNull Boolean execute(@NotNull Condition condition) {
        return toQuery(condition).fetchOne() != null;
    }

    @Override
    public @NotNull Select<Record1<Integer>> toQuery(@NotNull Condition condition) {
        return dsl().selectOne().whereExists(dsl().select(DSL.asterisk()).from(table()).where(condition));
    }

}
