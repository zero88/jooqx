package io.zero88.jpa;

/**
 * Abstract interface for pagination information.
 *
 * @since 1.0.0
 */
public interface Pageable {

    /**
     * Gets page number.
     *
     * @return Returns the page number.
     * @since 1.0.0
     */
    int getPage();

    /**
     * Get page size.
     *
     * @return Returns the number of record per page.
     * @since 1.0.0
     */
    int getPerPage();

}
