package io.zero88.rsql.jooq.criteria.comparison;

import java.util.List;

import org.jooq.Condition;
import org.jooq.Field;

import io.zero88.rsql.LikeWildcardPattern;
import io.zero88.rsql.jooq.JooqArgumentParser;
import io.zero88.rsql.jooq.criteria.JooqComparisonCriteriaBuilder;
import io.zero88.rsql.parser.ast.ComparisonOperatorProxy;

import lombok.NonNull;

public final class BetweenBuilder extends JooqComparisonCriteriaBuilder {

    @Override
    public @NonNull ComparisonOperatorProxy operator() {
        return ComparisonOperatorProxy.BETWEEN;
    }

    @Override
    protected @NonNull Condition compare(@NonNull Field field, @NonNull List<String> arguments,
                                         @NonNull JooqArgumentParser argParser,
                                         @NonNull LikeWildcardPattern wildcardPattern) {
        if (arguments.size() < 2) {
            throw new IllegalArgumentException("Between criteria requires 2 arguments");
        }
        return field.between(argParser.parse(field, arguments.get(0)), argParser.parse(field, arguments.get(1)));
    }

}
