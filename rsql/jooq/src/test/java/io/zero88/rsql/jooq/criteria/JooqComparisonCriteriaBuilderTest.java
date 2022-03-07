package io.zero88.rsql.jooq.criteria;

import java.util.Arrays;
import java.util.Collections;

import org.jetbrains.annotations.NotNull;
import org.jooq.Condition;
import org.jooq.meta.h2.information_schema.tables.Tables;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.github.zero88.utils.Strings;
import io.zero88.rsql.LikeWildcardPattern;
import io.zero88.rsql.jooq.JooqQueryContext;
import io.zero88.rsql.jooq.JooqRSQLContext;
import io.zero88.rsql.jooq.criteria.comparison.BetweenBuilder;
import io.zero88.rsql.jooq.criteria.comparison.ContainsBuilder;
import io.zero88.rsql.jooq.criteria.comparison.EndsWithBuilder;
import io.zero88.rsql.jooq.criteria.comparison.EqualBuilder;
import io.zero88.rsql.jooq.criteria.comparison.ExistsBuilder;
import io.zero88.rsql.jooq.criteria.comparison.GreaterThanBuilder;
import io.zero88.rsql.jooq.criteria.comparison.GreaterThanOrEqualBuilder;
import io.zero88.rsql.jooq.criteria.comparison.InBuilder;
import io.zero88.rsql.jooq.criteria.comparison.LessThanBuilder;
import io.zero88.rsql.jooq.criteria.comparison.LessThanOrEqualBuilder;
import io.zero88.rsql.jooq.criteria.comparison.LikeBuilder;
import io.zero88.rsql.jooq.criteria.comparison.NonExistsBuilder;
import io.zero88.rsql.jooq.criteria.comparison.NotEqualBuilder;
import io.zero88.rsql.jooq.criteria.comparison.NotInBuilder;
import io.zero88.rsql.jooq.criteria.comparison.NotLikeBuilder;
import io.zero88.rsql.jooq.criteria.comparison.NullableBuilder;
import io.zero88.rsql.jooq.criteria.comparison.StartsWithBuilder;
import io.zero88.rsql.parser.ast.ComparisonOperatorProxy;

import cz.jirutka.rsql.parser.ast.ComparisonNode;
import cz.jirutka.rsql.parser.ast.RSQLOperators;

public class JooqComparisonCriteriaBuilderTest {

    @Test
    public void test_equal_node() {
        final ComparisonNode node = new ComparisonNode(RSQLOperators.EQUAL, Tables.TABLES.TABLE_NAME.getName(),
                                                       Collections.singletonList("abc"));
        final JooqCriteriaBuilder builder = JooqCriteriaBuilderFactory.DEFAULT.create(node);
        Assertions.assertTrue(builder instanceof EqualBuilder);
        final Condition condition = builder.build(Tables.TABLES);
        Assertions.assertEquals("\"INFORMATION_SCHEMA\".\"TABLES\".\"TABLE_NAME\" = 'abc'", condition.toString());
    }

    @Test
    public void test_not_equal_node() {
        final ComparisonNode node = new ComparisonNode(RSQLOperators.NOT_EQUAL, Tables.TABLES.TABLE_NAME.getName(),
                                                       Collections.singletonList("abc"));
        final JooqCriteriaBuilder builder = JooqCriteriaBuilderFactory.DEFAULT.create(node);
        Assertions.assertTrue(builder instanceof NotEqualBuilder);
        final Condition condition = builder.build(Tables.TABLES);
        Assertions.assertEquals("\"INFORMATION_SCHEMA\".\"TABLES\".\"TABLE_NAME\" <> 'abc'", condition.toString());
    }

    @Test
    public void test_greater_than_node() {
        final ComparisonNode node = new ComparisonNode(RSQLOperators.GREATER_THAN,
                                                       Tables.TABLES.ROW_COUNT_ESTIMATE.getName(),
                                                       Collections.singletonList("5"));
        final JooqCriteriaBuilder builder = JooqCriteriaBuilderFactory.DEFAULT.create(node);
        Assertions.assertTrue(builder instanceof GreaterThanBuilder);
        final Condition condition = builder.build(Tables.TABLES);
        Assertions.assertEquals("\"INFORMATION_SCHEMA\".\"TABLES\".\"ROW_COUNT_ESTIMATE\" > 5", condition.toString());
    }

    @Test
    public void test_greater_than_or_equal_node() {
        final ComparisonNode node = new ComparisonNode(RSQLOperators.GREATER_THAN_OR_EQUAL, Tables.TABLES.ID.getName(),
                                                       Collections.singletonList("5"));
        final JooqCriteriaBuilder builder = JooqCriteriaBuilderFactory.DEFAULT.create(node);
        Assertions.assertTrue(builder instanceof GreaterThanOrEqualBuilder);
        final Condition condition = builder.build(Tables.TABLES);
        Assertions.assertEquals("\"INFORMATION_SCHEMA\".\"TABLES\".\"ID\" >= 5", condition.toString());
    }

    @Test
    public void test_less_than_node() {
        final ComparisonNode node = new ComparisonNode(RSQLOperators.LESS_THAN,
                                                       Tables.TABLES.ROW_COUNT_ESTIMATE.getName(),
                                                       Collections.singletonList("23"));
        final JooqCriteriaBuilder builder = JooqCriteriaBuilderFactory.DEFAULT.create(node);
        Assertions.assertTrue(builder instanceof LessThanBuilder);
        final Condition condition = builder.build(Tables.TABLES);
        Assertions.assertEquals("\"INFORMATION_SCHEMA\".\"TABLES\".\"ROW_COUNT_ESTIMATE\" < 23", condition.toString());
    }

    @Test
    public void test_less_than_or_equal_node() {
        final ComparisonNode node = new ComparisonNode(RSQLOperators.LESS_THAN_OR_EQUAL,
                                                       Tables.TABLES.ROW_COUNT_ESTIMATE.getName(),
                                                       Collections.singletonList("55"));
        final JooqCriteriaBuilder builder = JooqCriteriaBuilderFactory.DEFAULT.create(node);
        Assertions.assertTrue(builder instanceof LessThanOrEqualBuilder);
        final Condition condition = builder.build(Tables.TABLES);
        Assertions.assertEquals("\"INFORMATION_SCHEMA\".\"TABLES\".\"ROW_COUNT_ESTIMATE\" <= 55", condition.toString());
    }

    @Test
    public void test_in_node() {
        final ComparisonNode node = new ComparisonNode(RSQLOperators.IN, Tables.TABLES.ID.getName(),
                                                       Arrays.asList("5", "7", "10"));
        final JooqCriteriaBuilder builder = JooqCriteriaBuilderFactory.DEFAULT.create(node);
        Assertions.assertTrue(builder instanceof InBuilder);
        final Condition condition = builder.build(Tables.TABLES);
        Assertions.assertEquals("\"INFORMATION_SCHEMA\".\"TABLES\".\"ID\" in ( 5, 7, 10 )",
                                Strings.optimizeMultipleSpace(condition.toString()));
    }

    @Test
    public void test_not_in_node() {
        final ComparisonNode node = new ComparisonNode(RSQLOperators.NOT_IN, Tables.TABLES.ID.getName(),
                                                       Arrays.asList("5", "7", "10"));
        final JooqCriteriaBuilder builder = JooqCriteriaBuilderFactory.DEFAULT.create(node);
        Assertions.assertTrue(builder instanceof NotInBuilder);
        final Condition condition = builder.build(Tables.TABLES);
        Assertions.assertEquals("\"INFORMATION_SCHEMA\".\"TABLES\".\"ID\" not in ( 5, 7, 10 )",
                                Strings.optimizeMultipleSpace(condition.toString()));
    }

    @Test
    public void test_between_node() {
        final ComparisonNode node = new ComparisonNode(ComparisonOperatorProxy.BETWEEN.operator(),
                                                       Tables.TABLES.LAST_MODIFICATION.getName(),
                                                       Arrays.asList("10", "50"));
        final JooqCriteriaBuilder builder = JooqCriteriaBuilderFactory.DEFAULT.create(node);
        Assertions.assertTrue(builder instanceof BetweenBuilder);
        final Condition condition = builder.build(Tables.TABLES);
        System.out.println(node);
        System.out.println(condition);
        Assertions.assertEquals("\"INFORMATION_SCHEMA\".\"TABLES\".\"LAST_MODIFICATION\" between 10 and 50",
                                Strings.optimizeMultipleSpace(condition.toString()));
    }

    @Test
    public void test_exists_node() {
        final ComparisonNode node = new ComparisonNode(ComparisonOperatorProxy.EXISTS.operator(),
                                                       Tables.TABLES.TABLE_CLASS.getName(),
                                                       Collections.singletonList("t"));
        final JooqCriteriaBuilder builder = JooqCriteriaBuilderFactory.DEFAULT.create(node);
        Assertions.assertTrue(builder instanceof ExistsBuilder);
        final Condition condition = builder.build(Tables.TABLES);
        System.out.println(node);
        System.out.println(condition);
        Assertions.assertEquals("\"INFORMATION_SCHEMA\".\"TABLES\".\"TABLE_CLASS\" is not null",
                                Strings.optimizeMultipleSpace(condition.toString()));
    }

    @Test
    public void test_non_exists_node() {
        final ComparisonNode node = new ComparisonNode(ComparisonOperatorProxy.NON_EXISTS.operator(),
                                                       Tables.TABLES.TABLE_CLASS.getName(),
                                                       Collections.singletonList("t"));
        final JooqCriteriaBuilder builder = JooqCriteriaBuilderFactory.DEFAULT.create(node);
        Assertions.assertTrue(builder instanceof NonExistsBuilder);
        final Condition condition = builder.build(Tables.TABLES);
        Assertions.assertEquals("\"INFORMATION_SCHEMA\".\"TABLES\".\"TABLE_CLASS\" is null",
                                Strings.optimizeMultipleSpace(condition.toString()));
    }

    @Test
    public void test_nullable_node() {
        final ComparisonNode node = new ComparisonNode(ComparisonOperatorProxy.NULLABLE.operator(),
                                                       Tables.TABLES.TABLE_CLASS.getName(),
                                                       Collections.singletonList("t"));
        final JooqCriteriaBuilder builder = JooqCriteriaBuilderFactory.DEFAULT.create(node);
        Assertions.assertTrue(builder instanceof NullableBuilder);
        final Condition condition = builder.build(Tables.TABLES);
        Assertions.assertEquals("( \"INFORMATION_SCHEMA\".\"TABLES\".\"TABLE_CLASS\" is null " +
                                "or \"INFORMATION_SCHEMA\".\"TABLES\".\"TABLE_CLASS\" = 't' )",
                                Strings.optimizeMultipleSpace(condition.toString()));
    }

    @Test
    public void test_like_node() {
        final ComparisonNode node = new ComparisonNode(ComparisonOperatorProxy.LIKE.operator(),
                                                       Tables.TABLES.TABLE_CLASS.getName(),
                                                       Collections.singletonList("t?st"));
        final JooqCriteriaBuilder builder = JooqCriteriaBuilderFactory.DEFAULT.create(node);
        Assertions.assertTrue(builder instanceof LikeBuilder);
        final Condition condition = builder.build(Tables.TABLES);
        Assertions.assertEquals("\"INFORMATION_SCHEMA\".\"TABLES\".\"TABLE_CLASS\" like 't_st' escape '\\'",
                                Strings.optimizeMultipleSpace(condition.toString()));
    }

    @Test
    public void test_like_node_by_regex() {
        final ComparisonNode node = new ComparisonNode(ComparisonOperatorProxy.LIKE.operator(),
                                                       Tables.TABLES.TABLE_CLASS.getName(),
                                                       Collections.singletonList("t+"));
        final JooqCriteriaBuilder builder = JooqCriteriaBuilderFactory.DEFAULT.create(node);
        Assertions.assertTrue(builder instanceof LikeBuilder);
        final Condition condition = builder.build(JooqRSQLContext.create(Tables.TABLES, new JooqQueryContext() {
            @Override
            public @NotNull LikeWildcardPattern likeWildcard() {
                return LikeWildcardPattern.REGEX;
            }
        }));
        Assertions.assertEquals("(\"INFORMATION_SCHEMA\".\"TABLES\".\"TABLE_CLASS\" like_regex 't+')",
                                Strings.optimizeMultipleSpace(condition.toString()));
    }

    @Test
    public void test_notLike_node() {
        final ComparisonNode node = new ComparisonNode(ComparisonOperatorProxy.NOT_LIKE.operator(),
                                                       Tables.TABLES.TABLE_CLASS.getName(),
                                                       Collections.singletonList("*test%"));
        final JooqCriteriaBuilder builder = JooqCriteriaBuilderFactory.DEFAULT.create(node);
        Assertions.assertTrue(builder instanceof NotLikeBuilder);
        final Condition condition = builder.build(Tables.TABLES);
        Assertions.assertEquals("\"INFORMATION_SCHEMA\".\"TABLES\".\"TABLE_CLASS\" not like '%test\\%' escape '\\'",
                                Strings.optimizeMultipleSpace(condition.toString()));
    }

    @Test
    public void test_contains_node() {
        final ComparisonNode node = new ComparisonNode(ComparisonOperatorProxy.CONTAINS.operator(),
                                                       Tables.TABLES.TABLE_CLASS.getName(),
                                                       Collections.singletonList("test"));
        final JooqCriteriaBuilder builder = JooqCriteriaBuilderFactory.DEFAULT.create(node);
        Assertions.assertTrue(builder instanceof ContainsBuilder);
        final Condition condition = builder.build(Tables.TABLES);
        Assertions.assertEquals(
            "\"INFORMATION_SCHEMA\".\"TABLES\".\"TABLE_CLASS\" like ('%' || replace( replace( replace( 'test', '!', " +
            "'!!' ), '%', '!%' ), '_', '!_' ) || '%') escape '!'", Strings.optimizeMultipleSpace(condition.toString()));
    }

    @Test
    public void test_startswith_node() {
        final ComparisonNode node = new ComparisonNode(ComparisonOperatorProxy.STARTS_WITH.operator(),
                                                       Tables.TABLES.TABLE_CLASS.getName(),
                                                       Collections.singletonList("test"));
        final JooqCriteriaBuilder builder = JooqCriteriaBuilderFactory.DEFAULT.create(node);
        Assertions.assertTrue(builder instanceof StartsWithBuilder);
        final Condition condition = builder.build(Tables.TABLES);
        Assertions.assertEquals("\"INFORMATION_SCHEMA\".\"TABLES\".\"TABLE_CLASS\" like " +
                                "(replace( replace( replace( 'test', '!', '!!' ), '%', '!%' ), '_', '!_' ) || '%') " +
                                "escape '!'", Strings.optimizeMultipleSpace(condition.toString()));
    }

    @Test
    public void test_endswith_node() {
        final ComparisonNode node = new ComparisonNode(ComparisonOperatorProxy.ENDS_WITH.operator(),
                                                       Tables.TABLES.TABLE_CLASS.getName(),
                                                       Collections.singletonList("test"));
        final JooqCriteriaBuilder builder = JooqCriteriaBuilderFactory.DEFAULT.create(node);
        Assertions.assertTrue(builder instanceof EndsWithBuilder);
        final Condition condition = builder.build(Tables.TABLES);
        Assertions.assertEquals("\"INFORMATION_SCHEMA\".\"TABLES\".\"TABLE_CLASS\" like ('%' || " +
                                "replace( replace( replace( 'test', '!', '!!' ), '%', '!%' ), '_', '!_' )) escape '!'",
                                Strings.optimizeMultipleSpace(condition.toString()));
    }

}
