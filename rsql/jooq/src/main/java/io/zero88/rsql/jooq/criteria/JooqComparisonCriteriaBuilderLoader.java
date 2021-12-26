package io.zero88.rsql.jooq.criteria;

import io.zero88.rsql.criteria.ComparisonCriteriaBuilder;
import io.zero88.rsql.criteria.ComparisonCriteriaBuilderLoader;

/**
 * The service loader for {@code jOOQ} comparison criteria builder
 * <p>
 * A service provider that must be extended {@link JooqComparisonCriteriaBuilder}, is identified by placing a {@code
 * full name qualify name} in a provider-configuration file in the resource directory {@code
 * META-INF/services/io.zero88.rsql.jooq.criteria.JooqComparisonCriteriaBuilder}
 */
public final class JooqComparisonCriteriaBuilderLoader extends ComparisonCriteriaBuilderLoader {

    private static ComparisonCriteriaBuilderLoader instance;

    public static ComparisonCriteriaBuilderLoader getInstance() {
        if (instance == null) {
            synchronized (ComparisonCriteriaBuilderLoader.class) {
                instance = new JooqComparisonCriteriaBuilderLoader();
            }
        }
        return instance;
    }

    @Override
    protected Class<? extends ComparisonCriteriaBuilder> serviceClass() {
        return JooqComparisonCriteriaBuilder.class;
    }

}
