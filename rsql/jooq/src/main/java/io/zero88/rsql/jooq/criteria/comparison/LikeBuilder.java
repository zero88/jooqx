package io.zero88.rsql.jooq.criteria.comparison;

import java.util.List;

import org.jooq.Condition;
import org.jooq.Field;

import io.zero88.rsql.LikeWildcardPattern;
import io.zero88.rsql.jooq.JooqArgumentParser;
import io.zero88.rsql.jooq.criteria.JooqComparisonCriteriaBuilder;
import io.zero88.rsql.parser.ast.ComparisonOperatorProxy;

import lombok.NonNull;

public final class LikeBuilder extends JooqComparisonCriteriaBuilder {

    @Override
    public @NonNull ComparisonOperatorProxy operator() {
        return ComparisonOperatorProxy.LIKE;
    }

    @Override
    protected @NonNull Condition compare(@NonNull Field field, @NonNull List<String> arguments,
                                         @NonNull JooqArgumentParser argParser,
                                         @NonNull LikeWildcardPattern wildcardPattern) {
        return wildcardPattern.isRegexEnabled()
               ? field.likeRegex(arguments.get(0))
               : field.like(wildcardPattern.convert(arguments.get(0)), wildcardPattern.escape());
    }

}
