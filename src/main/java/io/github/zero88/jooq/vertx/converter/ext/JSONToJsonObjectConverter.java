package io.github.zero88.jooq.vertx.converter.ext;

import org.jooq.JSON;

import io.vertx.core.json.JsonObject;

/**
 * @author jensklingsporn
 */
public class JSONToJsonObjectConverter implements PgConverter<JsonObject, JSON, JsonObject> {

    private static final IdentityRowConverter<JsonObject> identityConverter = new IdentityRowConverter<>(
        JsonObject.class);

    private static JSONToJsonObjectConverter INSTANCE;

    public static JSONToJsonObjectConverter getInstance() {
        return INSTANCE == null ? INSTANCE = new JSONToJsonObjectConverter() : INSTANCE;
    }

    @Override
    public JsonObject from(JSON t) {
        return t == null ? null : new JsonObject(t.data());
    }

    @Override
    public JSON to(JsonObject u) {
        return u == null ? null : JSON.valueOf(u.encode());
    }

    @Override
    public Class<JSON> fromType() {
        return JSON.class;
    }

    @Override
    public Class<JsonObject> toType() {
        return JsonObject.class;
    }

    @Override
    public RowConverter<JsonObject, JsonObject> rowConverter() {
        return identityConverter;
    }

}
