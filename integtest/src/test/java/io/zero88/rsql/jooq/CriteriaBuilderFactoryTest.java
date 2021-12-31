package io.zero88.rsql.jooq;

import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.zero88.rsql.jooq.criteria.JooqCriteriaBuilder;
import io.zero88.rsql.jooq.criteria.JooqCriteriaBuilderFactory;
import io.zero88.rsql.jooq.criteria.comparison.EqualBuilder;
import io.zero88.rsql.jooq.criteria.logical.AndNodeCriteriaBuilder;
import io.zero88.rsql.jooq.criteria.logical.OrNodeCriteriaBuilder;

import cz.jirutka.rsql.parser.ast.AndNode;
import cz.jirutka.rsql.parser.ast.ComparisonNode;
import cz.jirutka.rsql.parser.ast.OrNode;
import cz.jirutka.rsql.parser.ast.RSQLOperators;

public class CriteriaBuilderFactoryTest {

    @Test
    public void test_comparison_node() {
        final JooqCriteriaBuilder builder = JooqCriteriaBuilderFactory.DEFAULT.create(
            new ComparisonNode(RSQLOperators.EQUAL, "test", Collections.singletonList("abc")));
        Assertions.assertTrue(builder instanceof EqualBuilder);
    }

    @Test
    public void test_and_node() {
        final JooqCriteriaBuilder builder = JooqCriteriaBuilderFactory.DEFAULT.create(new AndNode(
            Collections.singletonList(new ComparisonNode(RSQLOperators.IN, "test", Arrays.asList("abc", "xyz")))));
        Assertions.assertTrue(builder instanceof AndNodeCriteriaBuilder);
    }

    @Test
    public void test_or_node() {
        final JooqCriteriaBuilder builder = JooqCriteriaBuilderFactory.DEFAULT.create(new OrNode(
            Collections.singletonList(new ComparisonNode(RSQLOperators.IN, "test", Arrays.asList("abc", "xyz")))));
        Assertions.assertTrue(builder instanceof OrNodeCriteriaBuilder);
    }

}