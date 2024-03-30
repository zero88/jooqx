package io.github.zero88.integtest.jooqx.pg;

import org.jetbrains.annotations.NotNull;
import org.jooq.JSONB;

import io.github.zero88.jooqx.datatype.JooqxConverter;

public class JDBCJsonbConverter implements JooqxConverter<String, JSONB> {

    @Override
    public JSONB from(String vertxObject) {
        return JSONB.valueOf(vertxObject);
    }

    @Override
    public String to(JSONB jooqObject) {
        return jooqObject.toString();
    }

    @Override
    public @NotNull Class<String> fromType() {
        return String.class;
    }

    @Override
    public @NotNull Class<JSONB> toType() {
        return JSONB.class;
    }

}
