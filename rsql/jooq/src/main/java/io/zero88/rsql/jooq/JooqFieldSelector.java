package io.zero88.rsql.jooq;

import java.util.Collection;
import java.util.Collections;
import java.util.function.Supplier;

import org.jetbrains.annotations.NotNull;
import org.jooq.SelectFieldOrAsterisk;
import org.jooq.impl.DSL;

import io.zero88.rsql.FieldSelector;

/**
 * The interface Field selector.
 *
 * @since 1.0.0
 */
public interface JooqFieldSelector extends Supplier<Collection<? extends SelectFieldOrAsterisk>>, FieldSelector {

    /**
     * The constant DEFAULT.
     */
    JooqFieldSelector DEFAULT = () -> Collections.singleton(DSL.asterisk());

    /**
     * Get fields
     *
     * @return collection fields
     * @see SelectFieldOrAsterisk
     */
    @NotNull Collection<? extends SelectFieldOrAsterisk> get();

}
