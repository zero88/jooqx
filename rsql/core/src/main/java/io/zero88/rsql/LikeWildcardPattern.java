package io.zero88.rsql;

import java.util.regex.Pattern;

import io.github.zero88.utils.Strings;

/**
 * Represents for {@code wildcard pattern} that is using in {@code like}/{@code not-like} comparison, is applied in the
 * REST query.
 *
 * @see <a href="https://www.jcp.org/en/jsr/detail?id=317">JSR-317. Section 4.6.10 Like Expressions</a>
 * @since 1.0.0
 */
public interface LikeWildcardPattern {

    char JDBC_SEQ_CHAR = '%';
    char JDBC_SINGLE_CHAR = '_';
    char JDBC_ESCAPE_CHAR = '\\';

    /**
     * The default wildcard pattern
     */
    LikeWildcardPattern DEFAULT = () -> false;

    /**
     * The regex wildcard pattern
     */
    LikeWildcardPattern REGEX = () -> true;

    /**
     * Enable using regular expression in {@code like}/{@code not-like} comparison
     *
     * @return {@code true} if enabled, otherwise is {@code false}
     */
    boolean isRegexEnabled();

    /**
     * Stands for any sequence of characters (including the empty sequence)
     * <p>
     * It will be omitted if {@link #isRegexEnabled()} is {@code true}
     *
     * @return the sequence pattern
     */
    default char zeroOrMore() {
        return '*';
    }

    /**
     * Stands for any single character.
     * <p>
     * It will be omitted if {@link #isRegexEnabled()} is {@code true}
     *
     * @return the single pattern
     */
    default char single() {
        return '?';
    }

    /**
     * Stands for escape character.
     * <p>
     * It will be omitted if {@link #isRegexEnabled()} is {@code true}
     *
     * @return the escape character
     */
    default char escape() {
        return JDBC_ESCAPE_CHAR;
    }

    /**
     * Convert REST query argument to SQL query parameter based on the wildcard pattern
     *
     * @param argument the REST query argument
     * @return the SQL query parameter
     */
    default String convert(String argument) {
        if (Strings.isBlank(argument) || isRegexEnabled() ||
            (zeroOrMore() == JDBC_SEQ_CHAR && single() == JDBC_SINGLE_CHAR)) {
            return argument;
        }
        return argument.replaceAll(quote(JDBC_SEQ_CHAR, escape()), "" + escape() + escape() + JDBC_SEQ_CHAR)
                       .replaceAll(quote(JDBC_SINGLE_CHAR, escape()), "" + escape() + escape() + JDBC_SINGLE_CHAR)
                       .replaceAll(quote(zeroOrMore(), escape()), JDBC_SEQ_CHAR + "")
                       .replaceAll(quote(single(), escape()), JDBC_SINGLE_CHAR + "");
    }

    static String quote(char r, char escape) {
        return "(?<!" + Pattern.quote(escape + "") + ")" + Pattern.quote(r + "");
    }

}
