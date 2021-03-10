package io.github.zero88.jooq.vertx.converter;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ReactiveSQLConverter {

    public static ReactiveSQLPreparedQuery prepareQuery() {
        return new ReactiveSQLPreparedQueryImpl();
    }

    public static ReactiveSQLResultConverter resultSetConverter() {
        return new ReactiveSQLResultConverterImpl();
    }

    public static ReactiveSQLResultBatchConverter resultBatchConverter() {
        return new ReactiveSQLResultConverterImpl.ReactiveSQLResultBatchConverterImpl();
    }

}
