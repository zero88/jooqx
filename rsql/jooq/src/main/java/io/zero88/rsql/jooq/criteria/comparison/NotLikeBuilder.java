package io.zero88.rsql.jooq.criteria.comparison;

import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jooq.Condition;
import org.jooq.Field;

import io.zero88.rsql.LikeWildcardPattern;
import io.zero88.rsql.jooq.JooqArgumentParser;
import io.zero88.rsql.jooq.criteria.JooqComparisonCriteriaBuilder;
import io.zero88.rsql.parser.ast.ComparisonOperatorProxy;

public final class NotLikeBuilder extends JooqComparisonCriteriaBuilder {

    @Override
    public @NotNull ComparisonOperatorProxy operator() {
        return ComparisonOperatorProxy.NOT_LIKE;
    }

    @Override
    protected @NotNull Condition compare(@NotNull Field field, @NotNull List<String> arguments,
                                         @NotNull JooqArgumentParser argParser,
                                         @NotNull LikeWildcardPattern wildcardPattern) {
        return wildcardPattern.isRegexEnabled()
               ? field.notLikeRegex(arguments.get(0))
               : field.notLike(wildcardPattern.convert(arguments.get(0)), wildcardPattern.escape());
    }

}
