package io.github.zero88.jooq.vertx.datatype;

import java.util.function.Function;

import org.jooq.Converter;
import org.jooq.JSONB;

import io.vertx.core.json.JsonObject;

/**
 * @author jensklingsporn
 */
public class JSONBToJsonObjectBinding extends PGJsonToVertxJsonBinding<JSONB, JsonObject> {

    @Override
    public Converter<JSONB, JsonObject> converter() {
        return JSONBToJsonObjectConverter.getInstance();
    }

    @Override
    Function<String, JSONB> valueOf() {
        return JSONB::valueOf;
    }

    @Override
    String coerce() {
        return "::jsonb";
    }

}
