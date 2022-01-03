package io.zero88.integtest.jooqx.pg.jooq;

import org.jetbrains.annotations.NotNull;
import org.jooq.JSON;
import org.jooq.JSONB;

import io.zero88.jooqx.datatype.JooqxConverter;

class JDBCJsonbConverter implements JooqxConverter<String, JSONB> {

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
