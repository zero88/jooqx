package io.zero88.jooqx.provider;

import org.jetbrains.annotations.NotNull;

import io.vertx.core.json.JsonObject;

import lombok.NonNull;

/**
 * Provides an embedded database
 * <p>
 * For example: {@code H2}, {@code SQLite}
 *
 * @since 1.1.0
 */
public interface DBEmbeddedProvider extends DBProvider<String> {

    /**
     * Database name
     *
     * @return database name
     */
    @Override
    @NotNull String init();

    @Override
    default @NonNull JsonObject createConnOptions(String databaseName) {
        return new JsonObject().put("jdbcUrl", protocol() + databaseName).put("driverClassName", driverClassName());
    }

    /**
     * Local JDBC protocol
     *
     * @return JDBC protocol
     */
    @NotNull String protocol();

    /**
     * SQL Driver class name
     *
     * @return SQL driver class name
     */
    @NonNull String driverClassName();

    /**
     * Memory Database provider
     *
     * @since 1.1.0
     */
    interface DBMemoryProvider extends DBEmbeddedProvider {

    }


    /**
     * Local file database provider
     *
     * @since 1.1.0
     */
    interface DBFileProvider extends DBEmbeddedProvider {

        /**
         * Database username
         *
         * @return username
         */
        String user();

        /**
         * Database password
         *
         * @return password
         */
        String password();

        @Override
        @NonNull
        default JsonObject createConnOptions(String databaseName) {
            return DBEmbeddedProvider.super.createConnOptions(databaseName)
                                           .put("username", user())
                                           .put("password", password());
        }

    }

}