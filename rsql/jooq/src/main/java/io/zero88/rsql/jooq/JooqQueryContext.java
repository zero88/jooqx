package io.zero88.rsql.jooq;

import org.jetbrains.annotations.NotNull;

import io.zero88.rsql.QueryContext;

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
    default @NotNull JooqFieldSelector fieldSelector() {
        return JooqFieldSelector.DEFAULT;
    }

    /**
     * Field mapper.
     *
     * @return the field mapper
     * @see JooqFieldMapper
     * @since 1.0.0
     */
    default @NotNull JooqFieldMapper fieldMapper() {
        return JooqFieldMapper.DEFAULT;
    }

    /**
     * Argument parser.
     *
     * @return the argument parser
     * @see JooqArgumentParser
     * @since 1.0.0
     */
    default @NotNull JooqArgumentParser argumentParser() {
        return JooqArgumentParser.DEFAULT;
    }

}
