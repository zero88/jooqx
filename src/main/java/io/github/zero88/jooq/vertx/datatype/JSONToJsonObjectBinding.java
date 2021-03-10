package io.github.zero88.jooq.vertx.datatype;

import java.util.function.Function;

import org.jooq.Converter;
import org.jooq.JSON;

import io.vertx.core.json.JsonObject;

/**
 * @author jensklingsporn
 */
public class JSONToJsonObjectBinding extends PGJsonToVertxJsonBinding<JSON, JsonObject> {

    @Override
    public Converter<JSON, JsonObject> converter() {
        return JSONToJsonObjectConverter.getInstance();
    }

    @Override
    Function<String, JSON> valueOf() {
        return JSON::valueOf;
    }

    @Override
    String coerce() {
        return "::json";
    }

}
