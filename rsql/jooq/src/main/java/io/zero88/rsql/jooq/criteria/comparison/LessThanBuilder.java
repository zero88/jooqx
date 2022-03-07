package io.zero88.rsql.jooq.criteria.comparison;

import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jooq.Condition;
import org.jooq.Field;

import io.zero88.rsql.LikeWildcardPattern;
import io.zero88.rsql.jooq.JooqArgumentParser;
import io.zero88.rsql.jooq.criteria.JooqComparisonCriteriaBuilder;
import io.zero88.rsql.parser.ast.ComparisonOperatorProxy;

public final class LessThanBuilder extends JooqComparisonCriteriaBuilder {

    @Override
    public @NotNull ComparisonOperatorProxy operator() {
        return ComparisonOperatorProxy.LESS_THAN;
    }

    @Override
    protected @NotNull Condition compare(@NotNull Field field, @NotNull List<String> arguments,
                                         @NotNull JooqArgumentParser argParser,
                                         @NotNull LikeWildcardPattern wildcardPattern) {
        return field.lt(argParser.parse(field, arguments.get(0)));
    }

}
