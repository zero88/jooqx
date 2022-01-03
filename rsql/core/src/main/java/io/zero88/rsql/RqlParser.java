package io.zero88.rsql;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Set;

import io.github.zero88.exceptions.ReflectionException;
import io.github.zero88.repl.Arguments;
import io.github.zero88.repl.ReflectionClass;
import io.github.zero88.repl.ReflectionMethod;
import io.zero88.rsql.parser.ast.ComparisonOperatorProxy;

import cz.jirutka.rsql.parser.RSQLParserException;
import cz.jirutka.rsql.parser.ast.Node;

/**
 * Parser of the RSQL (RESTful Service Query Language).
 *
 * <p>RSQL is a query language for parametrized filtering of entries in RESTful APIs. It's a
 * superset of the <a href="http://tools.ietf.org/html/draft-nottingham-atompub-fiql-00">FIQL</a> (Feed Item Query
 * Language), so it can be used for parsing FIQL as well.</p>
 *
 * <p><b>Grammar in EBNF notation:</b>
 * <pre>{@code
 * input          = or, EOF;
 * or             = and, { ( "," | " or " ) , and };
 * and            = constraint, { ( ";" | " and " ), constraint };
 * constraint     = ( group | comparison );
 * group          = "(", or, ")";
 *
 * comparison     = selector, comparator, arguments;
 * selector       = unreserved-str;
 *
 * comparator     = comp-fiql | comp-alt;
 * comp-fiql      = ( ( "=", { ALPHA } ) | "!" ), "=";
 * comp-alt       = ( ">" | "<" ), [ "=" ];
 *
 * arguments      = ( "(", value, { "," , value }, ")" ) | value;
 * value          = unreserved-str | double-quoted | single-quoted;
 *
 * unreserved-str = unreserved, { unreserved }
 * single-quoted  = "'", { ( escaped | all-chars - ( "'" | "\" ) ) }, "'";
 * double-quoted  = '"', { ( escaped | all-chars - ( '"' | "\" ) ) }, '"';
 *
 * reserved       = '"' | "'" | "(" | ")" | ";" | "," | "=" | "!" | "~" | "<" | ">" | " ";
 * unreserved     = all-chars - reserved;
 * escaped        = "\", all-chars;
 * all-chars      = ? all unicode characters ?;
 * }</pre>
 *
 * @since 1.0.0
 */
public class RqlParser {

    private final NodesFactory nodesFactory;

    /**
     * Creates a new instance of {@code RSQLParser} that supports only the specified comparison operators.
     *
     * @param operators A set of supported comparison operators. Must not be <tt>null</tt> or empty.
     */
    protected RqlParser(Set<ComparisonOperatorProxy> operators) {
        if (operators == null || operators.isEmpty()) {
            throw new IllegalArgumentException("operators must not be null or empty");
        }
        this.nodesFactory = new NodesFactory(operators);
    }

    /**
     * Parses the RSQL expression and returns AST.
     *
     * @param query The query expression to parse.
     * @return A root of the parsed AST.
     * @throws RSQLParserException      If some exception occurred during parsing, i.e. the {@code query} is
     *                                  syntactically invalid.
     * @throws IllegalArgumentException If the {@code query} is <tt>null</tt>.
     */
    public final Node parse(String query) throws RSQLParserException {
        if (query == null) {
            throw new IllegalArgumentException("query must not be null");
        }
        InputStream is = new ByteArrayInputStream(query.getBytes(StandardCharsets.UTF_8));
        Arguments arguments = new Arguments().put(InputStream.class, is)
                                             .put(String.class, StandardCharsets.UTF_8.name())
                                             .put(cz.jirutka.rsql.parser.ast.NodesFactory.class, nodesFactory);
        Object object = Objects.requireNonNull(ReflectionClass.createObject("cz.jirutka.rsql.parser.Parser", arguments),
                                               "Unable init parser");
        Method method = ReflectionMethod.find(m -> m.getName().equals("Input"), object.getClass())
                                        .findFirst()
                                        .orElseThrow(() -> new IllegalStateException("Unable init parser"));
        try {
            return (Node) ReflectionMethod.execute(object, method);
        } catch (ReflectionException ex) {
            throw new RSQLParserException(ex.getCause());
        }
    }

}
