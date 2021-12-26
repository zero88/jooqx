package io.zero88.rsql;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.zero88.rsql.parser.ast.ComparisonOperatorProxy;

import cz.jirutka.rsql.parser.UnknownOperatorException;
import cz.jirutka.rsql.parser.ast.AndNode;
import cz.jirutka.rsql.parser.ast.ComparisonNode;
import cz.jirutka.rsql.parser.ast.LogicalNode;
import cz.jirutka.rsql.parser.ast.LogicalOperator;
import cz.jirutka.rsql.parser.ast.Node;
import cz.jirutka.rsql.parser.ast.OrNode;

public final class NodesFactory extends cz.jirutka.rsql.parser.ast.NodesFactory {

    private final Map<String, ComparisonOperatorProxy> comparisonOperators;

    public NodesFactory(Set<ComparisonOperatorProxy> operators) {
        super(new HashSet<>());
        comparisonOperators = new HashMap<>(operators.size());
        for (ComparisonOperatorProxy op : operators) {
            for (String sym : op.getSymbols()) {
                comparisonOperators.put(sym, op);
            }
        }
    }

    @Override
    public LogicalNode createLogicalNode(LogicalOperator operator, List<Node> children) {
        switch (operator) {
            case AND:
                return new AndNode(children);
            case OR:
                return new OrNode(children);
            // this normally can't happen
            default:
                throw new IllegalStateException("Unknown operator: " + operator);
        }
    }

    @Override
    public ComparisonNode createComparisonNode(String operatorToken, String selector, List<String> arguments)
        throws UnknownOperatorException {
        ComparisonOperatorProxy op = comparisonOperators.get(operatorToken);
        if (op != null) {
            return new ComparisonNode(op.operator(), selector, arguments);
        } else {
            throw new UnknownOperatorException(operatorToken);
        }
    }

}
