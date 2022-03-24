package io.zero88.jpa;

import java.io.Serializable;
import java.util.Collection;

import org.jetbrains.annotations.NotNull;

/**
 * Sort option for queries.
 *
 * @since 1.0.0
 */
public interface Sortable extends Serializable {

    /**
     * Get orders collection.
     *
     * @return the order collection
     * @since 1.0.0
     */
    @NotNull Collection<Order> orders();

    /**
     * Get order.
     *
     * @param property the property
     * @return the order
     * @since 1.0.0
     */
    Order get(String property);

    /**
     * Is empty boolean.
     *
     * @return the boolean
     * @since 1.0.0
     */
    boolean isEmpty();

}
