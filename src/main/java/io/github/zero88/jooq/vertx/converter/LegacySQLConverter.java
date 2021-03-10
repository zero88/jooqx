package io.github.zero88.jooq.vertx.converter;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class LegacySQLConverter {

    public static LegacySQLPreparedQuery prepareQuery() {
        return new LegacySQLPreparedQueryImpl();
    }

    public static LegacySQLResultConverter resultSetConverter() {
        return new LegacySQLResultConverterImpl();
    }

}
