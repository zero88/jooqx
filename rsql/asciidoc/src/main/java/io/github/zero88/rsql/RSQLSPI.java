package io.github.zero88.rsql;

import java.util.List;

import org.jooq.Condition;
import org.jooq.Field;

import io.vertx.docgen.Source;
import io.zero88.rsql.LikeWildcardPattern;
import io.zero88.rsql.jooq.JooqArgumentParser;
import io.zero88.rsql.jooq.criteria.JooqComparisonCriteriaBuilder;
import io.zero88.rsql.parser.ast.ComparisonOperatorProxy;

@Source
public class RSQLSPI {

    public static final class CustomOpBuilder extends JooqComparisonCriteriaBuilder {

        @Override
        public ComparisonOperatorProxy operator() {
            return ComparisonOperatorProxy.create("=custom=");
        }

        @Override
        protected Condition compare(Field field, List<String> arguments, JooqArgumentParser argParser,
            LikeWildcardPattern wildcardPattern) {
            // do something here
            throw new UnsupportedOperationException("Not yet implemented");
        }

    }

}
