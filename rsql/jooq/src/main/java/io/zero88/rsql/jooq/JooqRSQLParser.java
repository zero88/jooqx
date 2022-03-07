package io.zero88.rsql.jooq;

import java.util.Set;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jooq.Condition;
import org.jooq.TableLike;

import io.zero88.rsql.RSQLParser;
import io.zero88.rsql.jooq.criteria.JooqComparisonCriteriaBuilderLoader;
import io.zero88.rsql.jooq.visitor.JooqConditionVisitor;
import io.zero88.rsql.jooq.visitor.JooqRSQLVisitor;
import io.zero88.rsql.parser.ast.ComparisonOperatorProxy;

import cz.jirutka.rsql.parser.RSQLParserException;

/**
 * Represents for {@code jOOQ RQL} parser.
 *
 * @since 1.0.0
 */
@SuppressWarnings("rawtypes")
public final class JooqRSQLParser extends RSQLParser {

    /**
     * The constant DEFAULT.
     */
    public static final JooqRSQLParser DEFAULT = new JooqRSQLParser();

    /**
     * Instantiates a new {@code jOOQ RQL} parser with default Comparison Operator.
     *
     * @see JooqComparisonCriteriaBuilderLoader
     * @since 1.0.0
     */
    public JooqRSQLParser() {
        this(JooqComparisonCriteriaBuilderLoader.getInstance().operators());
    }

    /**
     * Instantiates a new {@code Jooq RQL} parser.
     *
     * @param comparisons the comparisons
     * @see ComparisonOperatorProxy
     * @since 1.0.0
     */
    public JooqRSQLParser(@NotNull Set<ComparisonOperatorProxy> comparisons) {
        super(comparisons);
    }

    /**
     * Parse query to Criteria condition.
     *
     * @param query the query
     * @param table the table
     * @return the condition
     * @throws RSQLParserException the RSQL parser exception
     * @see TableLike
     * @see Condition
     * @since 1.0.0
     */
    public Condition criteria(@NotNull String query, @NotNull TableLike table) throws RSQLParserException {
        return criteria(query, JooqRSQLContext.create(table));
    }

    /**
     * Parse query to Criteria condition.
     *
     * @param query   the query
     * @param context the visitor context
     * @return the condition
     * @throws RSQLParserException the RSQL parser exception
     * @see JooqConditionVisitor
     * @see Condition
     * @since 1.0.0
     */
    public Condition criteria(@NotNull String query, @NotNull JooqRSQLContext context) throws RSQLParserException {
        return execute(query, new JooqConditionVisitor(), context);
    }

    /**
     * Parse query and execute to the appropriate output.
     *
     * @param <R>            Type of {@code Visitor Result}
     * @param <C>            Type of {@code Visitor Context}
     * @param query          the query
     * @param visitor        the visitor
     * @param visitorContext the visitor context
     * @return the select condition step
     * @throws RSQLParserException the RSQL parser exception
     * @see JooqRSQLVisitor
     * @since 1.0.0
     */
    public <R, C extends JooqRSQLContext> R execute(@NotNull String query, @NotNull JooqRSQLVisitor<R, C> visitor,
        @Nullable C visitorContext) throws RSQLParserException {
        return parse(query).accept(visitor, visitorContext);
    }

}
