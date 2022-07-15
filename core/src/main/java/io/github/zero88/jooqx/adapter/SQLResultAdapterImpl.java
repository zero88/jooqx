package io.github.zero88.jooqx.adapter;

import org.jetbrains.annotations.NotNull;
import org.jooq.Record;

abstract class SQLResultAdapterImpl<ROW, RESULT> implements SQLResultAdapter<ROW, RESULT> {

    @NotNull
    private final RecordFactory<? extends Record, ROW> recordFactory;

    protected SQLResultAdapterImpl(@NotNull RecordFactory<? extends Record, ROW> recordFactory) {
        this.recordFactory = recordFactory;
    }

    public RecordFactory<? extends Record, ROW> recordFactory() {
        return recordFactory;
    }

}
