package io.zero88.jooqx;

import io.vertx.core.json.JsonObject;

import com.zaxxer.hikari.HikariConfig;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Helper to convert/parse SQL connection to Hikari config
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class HikariHelper {

    public static HikariConfig convert(SQLConnectionOption connOptions) {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(connOptions.getJdbcUrl());
        hikariConfig.setUsername(connOptions.getUser());
        hikariConfig.setPassword(connOptions.getPassword());
        hikariConfig.setDriverClassName(connOptions.getDriverClassName());
        return hikariConfig;
    }

    public static JsonObject parse(SQLConnectionOption connOptions) {
        return new JsonObject().put("jdbcUrl", connOptions.getJdbcUrl())
                               .put("username", connOptions.getUser())
                               .put("password", connOptions.getPassword())
                               .put("driverClassName", connOptions.getDriverClassName());
    }

}
