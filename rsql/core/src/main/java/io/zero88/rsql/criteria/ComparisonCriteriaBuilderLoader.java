package io.zero88.rsql.criteria;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import io.github.zero88.repl.ReflectionClass;
import io.github.zero88.utils.ServiceHelper;
import io.zero88.rsql.parser.ast.ComparisonOperatorProxy;

import cz.jirutka.rsql.parser.ast.ComparisonOperator;

/**
 * The service loader for the comparison criteria builder.
 *
 * @see ComparisonCriteriaBuilder
 * @since 1.0.0
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public abstract class ComparisonCriteriaBuilderLoader {

    private final Map<ComparisonOperatorProxy, Class<? extends ComparisonCriteriaBuilder>> map;

    protected ComparisonCriteriaBuilderLoader() {
        this.map = ServiceHelper.loadFactories(serviceClass())
                                .stream()
                                .collect(Collectors.toMap(ComparisonCriteriaBuilder::operator,
                                                          ComparisonCriteriaBuilder::getClass));
    }

    /**
     * Gets new comparison criteria builder instance by the given comparison operator
     *
     * @param operator the comparison operator
     * @return the comparison criteria builder or throw {@link IllegalArgumentException} if not found
     * @see ComparisonOperator
     */
    public ComparisonCriteriaBuilder get(ComparisonOperator operator) {
        return Optional.ofNullable(map.get(ComparisonOperatorProxy.wrap(operator)))
                       .map(ReflectionClass::createObject)
                       .orElseThrow(() -> new IllegalArgumentException(
                           "Not found criteria builder for [" + operator.getSymbol() + "]"));
    }

    /**
     * Gets list of the comparison operator proxy
     *
     * @return the comparison operator proxies
     * @see ComparisonOperatorProxy
     */
    public Set<ComparisonOperatorProxy> operators() {
        return map.keySet();
    }

    /**
     * Defines the comparison criteria builder class
     *
     * @return the comparison criteria builder class
     * @see ComparisonCriteriaBuilder
     */
    protected abstract Class<? extends ComparisonCriteriaBuilder> serviceClass();

}
