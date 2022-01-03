package io.zero88.rsql.criteria;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import io.github.zero88.repl.ReflectionClass;
import io.github.zero88.utils.ServiceHelper;
import io.zero88.rsql.parser.ast.ComparisonOperatorProxy;

import cz.jirutka.rsql.parser.ast.ComparisonOperator;

@SuppressWarnings({"unchecked", "rawtypes"})
public abstract class ComparisonCriteriaBuilderLoader {

    public static final Pattern SYMBOL_PATTERN = Pattern.compile("=[a-zA-Z]*=|[><]=?|!=");
    private final Map<ComparisonOperatorProxy, Class<? extends ComparisonCriteriaBuilder>> map;

    protected ComparisonCriteriaBuilderLoader() {
        this.map = ServiceHelper.loadFactories(serviceClass())
                                .stream()
                                .collect(Collectors.toMap(ComparisonCriteriaBuilder::operator,
                                                          ComparisonCriteriaBuilder::getClass));
    }

    public <T extends ComparisonOperatorProxy> ComparisonCriteriaBuilder<T> get(ComparisonOperator operator) {
        return Optional.ofNullable(map.get(ComparisonOperatorProxy.wrap(operator)))
                       .map(ReflectionClass::createObject)
                       .orElseThrow(() -> new IllegalArgumentException(
                           "Not found criteria builder for [" + operator.getSymbol() + "]"));
    }

    public Set<ComparisonOperatorProxy> operators() {
        return map.keySet();
    }

    protected abstract Class<? extends ComparisonCriteriaBuilder> serviceClass();

}
