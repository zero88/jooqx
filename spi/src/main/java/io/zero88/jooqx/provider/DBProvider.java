package io.zero88.jooqx.provider;

import org.jetbrains.annotations.NotNull;

import io.vertx.core.json.JsonObject;

/**
 * Provides database
 *
 * @param <DB> Type of database
 * @since 2.0.0
 */
public interface DBProvider<DB> {

    /**
     * Init database
     *
     * @return database
     */
    @NotNull DB init();

    /**
     * Create Database connection options
     *
     * @param database    database instance
     * @param connOptions extra connection options
     * @return SQL connection options
     */
    @NotNull JsonObject createConnOptions(@NotNull DB database, @NotNull JsonObject connOptions);

}
