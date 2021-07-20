package io.zero88.jooqx.provider;

import org.jetbrains.annotations.NotNull;

import io.vertx.core.json.JsonObject;

import lombok.NonNull;

/**
 * Provides database
 *
 * @param <DB> Type of database
 * @since 1.1.0
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
     * @param database database instance
     * @return SQL connection options
     */
    @NonNull JsonObject createConnOptions(DB database);

}
