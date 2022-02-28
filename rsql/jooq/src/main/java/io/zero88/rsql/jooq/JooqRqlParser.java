package io.zero88.rsql.jooq;

import java.util.Set;

import org.jetbrains.annotations.NotNull;
import org.jooq.Condition;
import org.jooq.TableLike;

import io.zero88.rsql.RqlParser;
import io.zero88.rsql.jooq.criteria.JooqComparisonCriteriaBuilderLoader;
import io.zero88.rsql.jooq.visitor.JooqConditionRqlVisitor;
import io.zero88.rsql.jooq.visitor.JooqRqlVisitor;
import io.zero88.rsql.parser.ast.ComparisonOperatorProxy;

import cz.jirutka.rsql.parser.RSQLParserException;

/**
 * Represents for {@code jOOQ RQL} parser.
 *
 * @since 1.0.0
 */
public final class JooqRqlParser extends RqlParser {

    /**
     * The constant DEFAULT.
     */
    public static final JooqRqlParser DEFAULT = new JooqRqlParser();

    /**
     * Instantiates a new {@code jOOQ RQL} parser with default Comparison Operator.
     *
     * @see JooqComparisonCriteriaBuilderLoader
     * @since 1.0.0
     */
    public JooqRqlParser() {
        this(JooqComparisonCriteriaBuilderLoader.getInstance().operators());
    }

    /**
     * Instantiates a new {@code Jooq RQL} parser.
     *
     * @param comparisons the comparisons
     * @see ComparisonOperatorProxy
     * @since 1.0.0
     */
    public JooqRqlParser(@NotNull Set<ComparisonOperatorProxy> comparisons) {
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
        return criteria(query, JooqConditionRqlVisitor.create(table));
    }

    /**
     * Parse query to Criteria condition.
     *
     * @param query   the query
     * @param visitor the visitor
     * @return the condition
     * @throws RSQLParserException the RSQL parser exception
     * @see JooqConditionRqlVisitor
     * @see Condition
     * @since 1.0.0
     */
    public Condition criteria(@NotNull String query, @NotNull JooqConditionRqlVisitor visitor)
        throws RSQLParserException {
        return parse(query, visitor, null);
    }

    /**
     * Parse query to appropriate output.
     *
     * @param <R>            Type of {@code Visitor Result}
     * @param <C>            Type of {@code Visitor Context}
     * @param query          the query
     * @param visitor        the visitor
     * @param visitorContext the visitor context
     * @return the select condition step
     * @throws RSQLParserException the RSQL parser exception
     * @see JooqRqlVisitor
     * @since 1.0.0
     */
    public <R, C> R parse(@NotNull String query, @NotNull JooqRqlVisitor<R, C> visitor, C visitorContext)
        throws RSQLParserException {
        return parse(query).accept(visitor, visitorContext);
    }

}
