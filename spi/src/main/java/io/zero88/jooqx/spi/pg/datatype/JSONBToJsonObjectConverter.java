package io.zero88.jooqx.spi.pg.datatype;

import org.jooq.JSONB;

import io.vertx.core.json.JsonObject;

/**
 * @author jensklingsporn
 */
public class JSONBToJsonObjectConverter implements PgConverter<JsonObject, JSONB, JsonObject> {

    private static final IdentityRowConverter<JsonObject> identityConverter = new IdentityRowConverter<>(
        JsonObject.class);

    private static JSONBToJsonObjectConverter INSTANCE;

    public static JSONBToJsonObjectConverter getInstance() {
        return INSTANCE == null ? INSTANCE = new JSONBToJsonObjectConverter() : INSTANCE;
    }

    @Override
    public JsonObject from(JSONB t) {
        return t == null ? null : new JsonObject(t.data());
    }

    @Override
    public JSONB to(JsonObject u) {
        return u == null ? null : JSONB.valueOf(u.encode());
    }

    @Override
    public Class<JSONB> fromType() {
        return JSONB.class;
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
