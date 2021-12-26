package io.zero88.rsql.jooq.ast;

import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.github.zero88.repl.ReflectionField;
import io.zero88.rsql.jooq.criteria.JooqComparisonCriteriaBuilderLoader;
import io.zero88.rsql.parser.ast.ComparisonOperatorProxy;

public class ComparisonOperatorProxyTest {

    @Test
    public void print() {
        final Set<ComparisonOperatorProxy> loaders = JooqComparisonCriteriaBuilderLoader.getInstance().operators();
        final Set<ComparisonOperatorProxy> constants = ReflectionField.streamConstants(ComparisonOperatorProxy.class)
                                                                      .collect(Collectors.toSet());
        Assertions.assertEquals(constants, loaders);
        constants.forEach(c -> System.out.println(c.operator()));
    }

}