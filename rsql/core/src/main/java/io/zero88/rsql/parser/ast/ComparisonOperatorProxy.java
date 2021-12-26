package io.zero88.rsql.parser.ast;

import java.util.Arrays;
import java.util.Objects;

import io.github.zero88.utils.Strings;
import io.zero88.rsql.criteria.ComparisonCriteriaBuilderLoader;

import cz.jirutka.rsql.parser.ast.ComparisonOperator;
import cz.jirutka.rsql.parser.ast.RSQLOperators;

/**
 * The interface Comparison operator proxy.
 * <p>
 * That helps to make more extension comparator or customize existence comparator
 *
 * @see ComparisonOperator
 * @since 1.0.0
 */
public final class ComparisonOperatorProxy {

    /**
     * The operator EQUAL.
     */
    public static final ComparisonOperatorProxy EQUAL = ComparisonOperatorProxy.wrap(RSQLOperators.EQUAL);
    /**
     * The operator NOT_EQUAL.
     */
    public static final ComparisonOperatorProxy NOT_EQUAL = ComparisonOperatorProxy.wrap(RSQLOperators.NOT_EQUAL);
    /**
     * The operator GREATER_THAN.
     */
    public static final ComparisonOperatorProxy GREATER_THAN = ComparisonOperatorProxy.wrap(RSQLOperators.GREATER_THAN);
    /**
     * The operator GREATER_THAN_OR_EQUAL.
     */
    // @formatter:off
    public static final ComparisonOperatorProxy GREATER_THAN_OR_EQUAL = ComparisonOperatorProxy.wrap(RSQLOperators.GREATER_THAN_OR_EQUAL);
    // @formatter:on
    /**
     * The operator LESS_THAN.
     */
    public static final ComparisonOperatorProxy LESS_THAN = ComparisonOperatorProxy.wrap(RSQLOperators.LESS_THAN);
    /**
     * The operator LESS_THAN_OR_EQUAL.
     */
    // @formatter:off
    public static final ComparisonOperatorProxy LESS_THAN_OR_EQUAL = ComparisonOperatorProxy.wrap(RSQLOperators.LESS_THAN_OR_EQUAL);
    // @formatter:on
    /**
     * The operator IN.
     */
    public static final ComparisonOperatorProxy IN = ComparisonOperatorProxy.wrap(RSQLOperators.IN);
    /**
     * The operator NOT_IN.
     */
    public static final ComparisonOperatorProxy NOT_IN = ComparisonOperatorProxy.wrap(RSQLOperators.NOT_IN);
    /**
     * The operator BETWEEN.
     */
    public static final ComparisonOperatorProxy BETWEEN = ComparisonOperatorProxy.multiValue("=between=");
    /**
     * The operator EXISTS.
     */
    public static final ComparisonOperatorProxy EXISTS = ComparisonOperatorProxy.create("=exists=", "=nn=");
    /**
     * The operator NON_EXISTS.
     */
    public static final ComparisonOperatorProxy NON_EXISTS = ComparisonOperatorProxy.create("=null=", "=isn=");
    /**
     * The operator NULLABLE.
     */
    public static final ComparisonOperatorProxy NULLABLE = ComparisonOperatorProxy.create("=nullable=");
    /**
     * The operator LIKE
     */
    public static final ComparisonOperatorProxy LIKE = ComparisonOperatorProxy.create("=like=");
    /**
     * The operator NOT_LIKE
     */
    public static final ComparisonOperatorProxy NOT_LIKE = ComparisonOperatorProxy.create("=unlike=", "=nk=");
    /**
     * The operator CONTAINS
     */
    public static final ComparisonOperatorProxy CONTAINS = ComparisonOperatorProxy.create("=contains=");
    /**
     * The operator STARTS_WITH
     */
    public static final ComparisonOperatorProxy STARTS_WITH = ComparisonOperatorProxy.create("=startswith=", "=sw=");
    /**
     * The operator ENDS_WITH
     */
    public static final ComparisonOperatorProxy ENDS_WITH = ComparisonOperatorProxy.create("=endswith=", "=ew=");

    private final ComparisonOperator operator;
    private final String[] symbols;

    private ComparisonOperatorProxy(ComparisonOperator operator) {
        this.operator = operator;
        this.symbols = null;
    }

    private ComparisonOperatorProxy(boolean multiValue, String... symbols) {
        this.symbols = Arrays.stream(symbols).filter(Strings::isNotBlank).toArray(String[]::new);
        String[] originSymbols = Arrays.stream(this.symbols)
                                       .filter(s -> ComparisonCriteriaBuilderLoader.SYMBOL_PATTERN.matcher(s).matches())
                                       .toArray(String[]::new);
        if (this.symbols.length == 0 || originSymbols.length == 0) {
            throw new IllegalArgumentException(
                "Missing Comparison operator or at least one of the symbols must match " +
                ComparisonCriteriaBuilderLoader.SYMBOL_PATTERN);
        }
        this.operator = new ComparisonOperator(originSymbols, multiValue);
    }

    public static ComparisonOperatorProxy wrap(ComparisonOperator operator) {
        return new ComparisonOperatorProxy(operator);
    }

    public static ComparisonOperatorProxy create(String symbol) {
        return new ComparisonOperatorProxy(false, symbol);
    }

    public static ComparisonOperatorProxy create(String... symbols) {
        return new ComparisonOperatorProxy(false, symbols);
    }

    public static ComparisonOperatorProxy multiValue(String symbol) {
        return new ComparisonOperatorProxy(true, symbol);
    }

    public static ComparisonOperatorProxy multiValue(String... symbols) {
        return new ComparisonOperatorProxy(true, symbols);
    }

    /**
     * Gets comparison operator.
     *
     * @return the comparison operator
     * @see ComparisonOperator
     * @since 1.0.0
     */
    public ComparisonOperator operator() {
        return operator;
    }

    public String[] getSymbols() {
        return Objects.isNull(symbols) ? operator.getSymbols() : symbols;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ComparisonOperatorProxy that = (ComparisonOperatorProxy) o;
        return operator.equals(that.operator);
    }

    @Override
    public int hashCode() {
        return operator.hashCode();
    }

}
