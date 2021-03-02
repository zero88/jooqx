package io.github.zero88.jooq.vertx.converter;

import java.util.Collection;

import org.jooq.Param;

import io.vertx.core.json.JsonArray;

public class LegacyParamConverter implements ParamConverter<JsonArray> {

    @Override
    public JsonArray convert(Collection<Param<?>> params) {
        JsonArray array = new JsonArray();
        for (Param<?> param : params) {
            if (!param.isInline()) {
                array.add(convertToDatabaseType(param));
            }
        }
        return array;
    }

}
