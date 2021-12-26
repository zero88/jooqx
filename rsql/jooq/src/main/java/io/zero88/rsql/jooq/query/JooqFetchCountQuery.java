package io.zero88.rsql.jooq.query;

import org.jooq.Condition;
import org.jooq.Record1;
import org.jooq.Select;

import lombok.NonNull;
import lombok.experimental.SuperBuilder;

/**
 * Represents for jOOQ fetch count query.
 *
 * @see JooqConditionQuery
 * @since 1.0.0
 */
@SuperBuilder
public final class JooqFetchCountQuery extends AbstractJooqConditionQuery<Integer> {

    @Override
    public Integer execute(@NonNull Condition condition) {
        return toQuery(condition).fetchOptional().map(Record1::value1).orElse(0);
    }

    @Override
    public @NonNull Select<Record1<Integer>> toQuery(@NonNull Condition condition) {
        return dsl().selectCount().from(table()).where(condition);
    }

}