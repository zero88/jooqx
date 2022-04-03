package io.zero88.jooqx.provider;

import java.sql.Driver;
import java.util.UUID;

import org.jetbrains.annotations.NotNull;

import io.vertx.core.json.JsonObject;

/**
 * Provides an embedded database
 * <p>
 * For example: {@code H2}, {@code SQLite}
 *
 * @since 1.1.0
 */
public interface DBEmbeddedProvider extends DBProvider<String> {

    /**
     * Provides Database name
     *
     * @return database name
     */
    @Override
    @NotNull String init();

    @Override
    default @NotNull JsonObject createConnOptions(@NotNull String databaseName, @NotNull JsonObject connOptions) {
        return new JsonObject().put("jdbcUrl", protocol() + databaseName)
                               .put("driverClassName", driverClassName())
                               .mergeIn(connOptions, true);
    }

    /**
     * JDBC protocol
     *
     * @return JDBC protocol
     */
    @NotNull String protocol();

    /**
     * SQL Driver class name
     *
     * @return SQL driver class name
     * @see Driver
     */
    @NotNull String driverClassName();

    /**
     * Memory Database provider
     *
     * @since 1.1.0
     */
    interface DBMemoryProvider extends DBEmbeddedProvider {

        @Override
        default @NotNull String init() {
            return UUID.randomUUID().toString();
        }

    }


    /**
     * Local file database provider
     *
     * @since 1.1.0
     */
    interface DBFileProvider extends DBEmbeddedProvider {

        /**
         * Extracts database username
         *
         * @param connOptions SQL connection options
         * @return username
         * @apiNote Default is admin
         */
        default String user(JsonObject connOptions) {
            return "admin";
        }

        /**
         * Extracts database password
         *
         * @param connOptions SQL connection options
         * @return password
         * @apiNote Default is no password
         */
        default String password(JsonObject connOptions) {
            return null;
        }

        @Override
        default @NotNull JsonObject createConnOptions(@NotNull String databaseName, @NotNull JsonObject connOptions) {
            return DBEmbeddedProvider.super.createConnOptions(databaseName, connOptions)
                                           .put("user", user(connOptions))
                                           .put("username", user(connOptions))
                                           .put("password", password(connOptions));
        }

    }

}
