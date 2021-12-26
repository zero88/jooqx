package io.zero88.rsql.jooq;

import io.zero88.rsql.QueryContext;

import lombok.NonNull;

/**
 * The interface Query context.
 *
 * @since 1.0.0
 */
public interface JooqQueryContext extends QueryContext {

    /**
     * The constant DEFAULT.
     */
    JooqQueryContext DEFAULT = new JooqQueryContext() {};

    /**
     * Field selector.
     *
     * @return the field selector
     * @see JooqFieldSelector
     * @since 1.0.0
     */
    default @NonNull JooqFieldSelector fieldSelector() {
        return JooqFieldSelector.DEFAULT;
    }

    /**
     * Field mapper.
     *
     * @return the field mapper
     * @see JooqFieldMapper
     * @since 1.0.0
     */
    default @NonNull JooqFieldMapper fieldMapper() {
        return JooqFieldMapper.DEFAULT;
    }

    /**
     * Argument parser.
     *
     * @return the argument parser
     * @see JooqArgumentParser
     * @since 1.0.0
     */
    default @NonNull JooqArgumentParser argumentParser() {
        return JooqArgumentParser.DEFAULT;
    }

}
