package io.github.zero88.integtest.jooqx.pg.jooq;

import org.jetbrains.annotations.NotNull;
import org.jooq.JSON;

import io.github.zero88.jooqx.datatype.JooqxConverter;

class JDBCJsonConverter implements JooqxConverter<String, JSON> {

    @Override
    public JSON from(String vertxObject) {
        return JSON.valueOf(vertxObject);
    }

    @Override
    public String to(JSON jooqObject) {
        return jooqObject.toString();
    }

    @Override
    public @NotNull Class<String> fromType() {
        return String.class;
    }

    @Override
    public @NotNull Class<JSON> toType() {
        return JSON.class;
    }

}
