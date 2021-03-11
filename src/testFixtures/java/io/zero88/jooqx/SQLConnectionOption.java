package io.zero88.jooqx;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SQLConnectionOption {

    private final String host;
    private final int port;
    private final String database;
    private final String username;
    private final String password;
    private final String jdbcUrl;
    private final String driverClassName;

}
