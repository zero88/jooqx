package io.zero88.rsql.jooq;

import java.util.Arrays;
import java.util.Collections;

import org.jooq.Condition;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.github.zero88.utils.Strings;
import io.zero88.rsql.jooq.criteria.JooqCriteriaBuilderFactory;
import io.zero88.sample.data.h2.Tables;

import cz.jirutka.rsql.parser.ast.AndNode;
import cz.jirutka.rsql.parser.ast.ComparisonNode;
import cz.jirutka.rsql.parser.ast.OrNode;
import cz.jirutka.rsql.parser.ast.RSQLOperators;

public class CriteriaBuilderTest {

    @Test
    public void test_and_node() {
        final ComparisonNode eqNode1 = new ComparisonNode(RSQLOperators.EQUAL, Tables.ALLDATATYPES.F_STR.getName(),
                                                          Collections.singletonList("abc"));
        final ComparisonNode eqNode2 = new ComparisonNode(RSQLOperators.EQUAL, Tables.ALLDATATYPES.F_BOOL.getName(),
                                                          Collections.singletonList("true"));
        final AndNode node = new AndNode(Arrays.asList(eqNode1, eqNode2));
        final Condition condition = JooqCriteriaBuilderFactory.DEFAULT.create(node).build(Tables.ALLDATATYPES);
        System.out.println(node);
        System.out.println(condition);
        Assertions.assertEquals("( \"ALLDATATYPES\".\"F_STR\" = 'abc' and \"ALLDATATYPES\".\"F_BOOL\" = true )",
                                Strings.optimizeMultipleSpace(condition.toString()));
    }

    @Test
    public void test_or_node() {
        final ComparisonNode eqNode1 = new ComparisonNode(RSQLOperators.EQUAL, Tables.ALLDATATYPES.F_DURATION.getName(),
                                                          Collections.singletonList("abc"));
        final ComparisonNode eqNode2 = new ComparisonNode(RSQLOperators.EQUAL, Tables.ALLDATATYPES.F_PERIOD.getName(),
                                                          Collections.singletonList("xyz"));
        final OrNode node = new OrNode(Arrays.asList(eqNode1, eqNode2));
        final Condition condition = JooqCriteriaBuilderFactory.DEFAULT.create(node).build(Tables.ALLDATATYPES);
        System.out.println(node);
        System.out.println(condition);
        Assertions.assertEquals("( \"ALLDATATYPES\".\"F_DURATION\" = 'abc' or \"ALLDATATYPES\".\"F_PERIOD\" = 'xyz' )",
                                Strings.optimizeMultipleSpace(condition.toString()));
    }

    @Test
    public void test_combine_logical_node() {
        final ComparisonNode eqNode1 = new ComparisonNode(RSQLOperators.EQUAL, Tables.ALLDATATYPES.F_STR.getName(),
                                                          Collections.singletonList("abc"));
        final ComparisonNode eqNode2 = new ComparisonNode(RSQLOperators.EQUAL, Tables.ALLDATATYPES.F_BOOL.getName(),
                                                          Collections.singletonList("true"));
        final ComparisonNode eqNode3 = new ComparisonNode(RSQLOperators.EQUAL, Tables.ALLDATATYPES.F_DURATION.getName(),
                                                          Collections.singletonList("def"));
        final ComparisonNode eqNode4 = new ComparisonNode(RSQLOperators.EQUAL, Tables.ALLDATATYPES.F_PERIOD.getName(),
                                                          Collections.singletonList("xyz"));
        final OrNode orNode = new OrNode(Arrays.asList(eqNode3, eqNode4));
        final AndNode node = new AndNode(Arrays.asList(eqNode1, eqNode2, orNode));
        final Condition condition = JooqCriteriaBuilderFactory.DEFAULT.create(node).build(Tables.ALLDATATYPES);
        System.out.println(node);
        System.out.println(condition);
        Assertions.assertEquals(
            "( \"ALLDATATYPES\".\"F_STR\" = 'abc' and \"ALLDATATYPES\".\"F_BOOL\" = true and ( \"ALLDATATYPES\"" +
            ".\"F_DURATION\" = 'def' or \"ALLDATATYPES\".\"F_PERIOD\" = 'xyz' ) )",
            Strings.optimizeMultipleSpace(condition.toString()));
    }

}
