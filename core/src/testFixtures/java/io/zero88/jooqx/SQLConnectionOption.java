package io.zero88.jooqx;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;

@DataObject(generateConverter = true)
public class SQLConnectionOption {

    private String host;
    private int port;
    private String database;
    private String user;
    private String password;
    private String jdbcUrl;
    private String driverClassName;

    public SQLConnectionOption() {}

    public SQLConnectionOption(JsonObject json) {
        SQLConnectionOptionConverter.fromJson(json, this);
    }

    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        SQLConnectionOptionConverter.toJson(this, json);
        return json;
    }

    public String getHost()            {return this.host;}

    public int getPort()               {return this.port;}

    public String getDatabase()        {return this.database;}

    public String getUser()            {return this.user;}

    public String getPassword()        {return this.password;}

    public String getJdbcUrl()         {return this.jdbcUrl;}

    public String getDriverClassName() {return this.driverClassName;}

    public SQLConnectionOption setHost(String host) {
        this.host = host;
        return this;
    }

    public SQLConnectionOption setPort(int port) {
        this.port = port;
        return this;
    }

    public SQLConnectionOption setDatabase(String database) {
        this.database = database;
        return this;
    }

    public SQLConnectionOption setUser(String user) {
        this.user = user;
        return this;
    }

    public SQLConnectionOption setPassword(String password) {
        this.password = password;
        return this;
    }

    public SQLConnectionOption setJdbcUrl(String jdbcUrl) {
        this.jdbcUrl = jdbcUrl;
        return this;
    }

    public SQLConnectionOption setDriverClassName(String driverClassName) {
        this.driverClassName = driverClassName;
        return this;
    }

}
