package io.zero88.jooqx.datatype;

import org.jooq.JSONB;

import io.vertx.core.json.JsonArray;

/**
 * @author jensklingsporn
 */
public class JSONBToJsonArrayConverter implements PgConverter<JsonArray, JSONB, JsonArray> {

    private static final IdentityRowConverter<JsonArray> identityConverter = new IdentityRowConverter<>(
        JsonArray.class);

    private static JSONBToJsonArrayConverter INSTANCE;

    public static JSONBToJsonArrayConverter getInstance() {
        return INSTANCE == null ? INSTANCE = new JSONBToJsonArrayConverter() : INSTANCE;
    }

    @Override
    public JsonArray from(JSONB t) {
        return t == null ? null : new JsonArray(t.data());
    }

    @Override
    public JSONB to(JsonArray u) {
        return u == null ? null : JSONB.valueOf(u.encode());
    }

    @Override
    public Class<JSONB> fromType() {
        return JSONB.class;
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
