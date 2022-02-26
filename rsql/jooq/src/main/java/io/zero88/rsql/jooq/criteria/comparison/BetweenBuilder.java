package io.zero88.rsql.jooq.criteria.comparison;

import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jooq.Condition;
import org.jooq.Field;

import io.zero88.rsql.LikeWildcardPattern;
import io.zero88.rsql.jooq.JooqArgumentParser;
import io.zero88.rsql.jooq.criteria.JooqComparisonCriteriaBuilder;
import io.zero88.rsql.parser.ast.ComparisonOperatorProxy;

public final class BetweenBuilder extends JooqComparisonCriteriaBuilder {

    @Override
    public @NotNull ComparisonOperatorProxy operator() {
        return ComparisonOperatorProxy.BETWEEN;
    }

    @Override
    protected @NotNull Condition compare(@NotNull Field field, @NotNull List<String> arguments,
                                         @NotNull JooqArgumentParser argParser,
                                         @NotNull LikeWildcardPattern wildcardPattern) {
        if (arguments.size() < 2) {
            throw new IllegalArgumentException("Between criteria requires 2 arguments");
        }
        return field.between(argParser.parse(field, arguments.get(0)), argParser.parse(field, arguments.get(1)));
    }

}
