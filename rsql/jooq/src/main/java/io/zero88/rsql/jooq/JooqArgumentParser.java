package io.zero88.rsql.jooq;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.jooq.Field;

import io.zero88.rsql.ArgumentParser;
import io.github.zero88.utils.DateTimes;
import io.github.zero88.utils.DateTimes.Iso8601Parser;
import io.github.zero88.utils.Strings;

import lombok.NonNull;

/**
 * The interface Argument parser.
 *
 * @since 1.0.0
 */
public interface JooqArgumentParser extends ArgumentParser {

    /**
     * The constant DEFAULT.
     */
    JooqArgumentParser DEFAULT = new JooqArgumentParser() {};

    /**
     * Parse one argument value.
     *
     * @param field the database field
     * @param value the argument value
     * @return the database value
     * @see Field
     * @since 1.0.0
     */
    default Object parse(@NonNull Field field, String value) {
        if (Strings.isBlank(value)) {
            return null;
        }
        if (!DateTimes.isRelatedToDateTime(field.getDataType().getType())) {
            return value;
        }
        return Optional.ofNullable(field.getDataType().convert(value))
                       .orElseGet(() -> Iso8601Parser.parse(field.getDataType().getType(), value));
    }

    /**
     * Parse list argument values.
     *
     * @param field  the database field
     * @param values the argument values
     * @return the database values
     * @see Field
     * @since 1.0.0
     */
    default List<?> parse(@NonNull Field field, @NonNull List<String> values) {
        if (!DateTimes.isRelatedToDateTime(field.getDataType().getType())) {
            return values;
        }
        return values.stream().map(s -> parse(field, s)).collect(Collectors.toList());
    }

}
