package io.zero88.rsql.jooq.criteria;

import java.util.Arrays;
import java.util.Collections;

import org.jooq.Condition;
import org.jooq.meta.h2.information_schema.tables.Tables;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.github.zero88.utils.Strings;

import cz.jirutka.rsql.parser.ast.AndNode;
import cz.jirutka.rsql.parser.ast.ComparisonNode;
import cz.jirutka.rsql.parser.ast.OrNode;
import cz.jirutka.rsql.parser.ast.RSQLOperators;

public class JooqCriteriaBuilderTest {

    @Test
    public void test_and_node() {
        final ComparisonNode eqNode1 = new ComparisonNode(RSQLOperators.EQUAL, Tables.TABLES.TABLE_NAME.getName(),
                                                          Collections.singletonList("abc"));
        final ComparisonNode eqNode2 = new ComparisonNode(RSQLOperators.EQUAL, Tables.TABLES.TABLE_CLASS.getName(),
                                                          Collections.singletonList("true"));
        final AndNode node = new AndNode(Arrays.asList(eqNode1, eqNode2));
        final Condition condition = JooqCriteriaBuilderFactory.DEFAULT.create(node).build(Tables.TABLES);
        System.out.println(node);
        System.out.println(condition);
        Assertions.assertEquals("( \"INFORMATION_SCHEMA\".\"TABLES\".\"TABLE_NAME\" = 'abc' and " +
                                "\"INFORMATION_SCHEMA\".\"TABLES\".\"TABLE_CLASS\" = 'true' )",
                                Strings.optimizeMultipleSpace(condition.toString()));
    }

    @Test
    public void test_or_node() {
        final ComparisonNode eqNode1 = new ComparisonNode(RSQLOperators.EQUAL, Tables.TABLES.TABLE_NAME.getName(),
                                                          Collections.singletonList("abc"));
        final ComparisonNode eqNode2 = new ComparisonNode(RSQLOperators.EQUAL, Tables.TABLES.TABLE_CLASS.getName(),
                                                          Collections.singletonList("xyz"));
        final OrNode node = new OrNode(Arrays.asList(eqNode1, eqNode2));
        final Condition condition = JooqCriteriaBuilderFactory.DEFAULT.create(node).build(Tables.TABLES);
        System.out.println(node);
        System.out.println(condition);
        Assertions.assertEquals("( \"INFORMATION_SCHEMA\".\"TABLES\".\"TABLE_NAME\" = 'abc' or \"INFORMATION_SCHEMA\"" +
                                ".\"TABLES\".\"TABLE_CLASS\" = 'xyz' )",
                                Strings.optimizeMultipleSpace(condition.toString()));
    }

    @Test
    public void test_combine_logical_node() {
        final ComparisonNode eqNode1 = new ComparisonNode(RSQLOperators.EQUAL, Tables.TABLES.TABLE_NAME.getName(),
                                                          Collections.singletonList("abc"));
        final ComparisonNode eqNode2 = new ComparisonNode(RSQLOperators.EQUAL, Tables.TABLES.TABLE_CLASS.getName(),
                                                          Collections.singletonList("true"));
        final ComparisonNode eqNode3 = new ComparisonNode(RSQLOperators.EQUAL, Tables.TABLES.TABLE_SCHEMA.getName(),
                                                          Collections.singletonList("def"));
        final ComparisonNode eqNode4 = new ComparisonNode(RSQLOperators.EQUAL, Tables.TABLES.TABLE_TYPE.getName(),
                                                          Collections.singletonList("xyz"));
        final OrNode orNode = new OrNode(Arrays.asList(eqNode3, eqNode4));
        final AndNode node = new AndNode(Arrays.asList(eqNode1, eqNode2, orNode));
        final Condition condition = JooqCriteriaBuilderFactory.DEFAULT.create(node).build(Tables.TABLES);
        System.out.println(node);
        System.out.println(condition);
        Assertions.assertEquals(
            "( \"INFORMATION_SCHEMA\".\"TABLES\".\"TABLE_NAME\" = 'abc' and \"INFORMATION_SCHEMA\".\"TABLES\"" +
            ".\"TABLE_CLASS\" = 'true' and ( \"INFORMATION_SCHEMA\".\"TABLES\".\"TABLE_SCHEMA\" = 'def' or " +
            "\"INFORMATION_SCHEMA\".\"TABLES\".\"TABLE_TYPE\" = 'xyz' ) )",
            Strings.optimizeMultipleSpace(condition.toString()));
    }

}
