package io.zero88.jooqx.datatype.basic;

import org.jetbrains.annotations.NotNull;
import org.jooq.JSONB;

import io.vertx.core.json.JsonArray;
import io.zero88.jooqx.datatype.JooqxConverter;

public final class JsonArrayJSONBConverter implements JooqxConverter<JsonArray, JSONB> {

    @Override
    public JSONB from(JsonArray vertxObject) {
        return vertxObject == null ? null : JSONB.valueOf(vertxObject.encode());
    }

    @Override
    public JsonArray to(JSONB jooqObject) { return jooqObject == null ? null : new JsonArray(jooqObject.data()); }

    @Override
    public @NotNull Class<JsonArray> fromType() { return JsonArray.class; }

    @Override
    public @NotNull Class<JSONB> toType() { return JSONB.class; }

}
