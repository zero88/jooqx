package io.github.zero88.jooq.vertx.datatype;

import org.jooq.JSON;

import io.vertx.core.json.JsonArray;

/**
 * @author jensklingsporn
 */
public class JSONToJsonArrayConverter implements PgConverter<JsonArray, JSON, JsonArray> {

    private static final IdentityRowConverter<JsonArray> identityConverter = new IdentityRowConverter<>(
        JsonArray.class);

    private static JSONToJsonArrayConverter INSTANCE;

    public static JSONToJsonArrayConverter getInstance() {
        return INSTANCE == null ? INSTANCE = new JSONToJsonArrayConverter() : INSTANCE;
    }

    @Override
    public JsonArray from(JSON t) {
        return t == null ? null : new JsonArray(t.data());
    }

    @Override
    public JSON to(JsonArray u) {
        return u == null ? null : JSON.valueOf(u.encode());
    }

    @Override
    public Class<JSON> fromType() {
        return JSON.class;
    }

    @Override
    public Class<JsonArray> toType() {
        return JsonArray.class;
    }

    @Override
    public RowConverter<JsonArray, JsonArray> rowConverter() {
        return identityConverter;
    }

}
