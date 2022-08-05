package io.github.zero88.jooqx;

import java.util.List;

import org.jetbrains.annotations.NotNull;

final class BatchReturningResultImpl<R> extends BatchResultImpl implements BatchReturningResult<R> {

    @NotNull
    private final List<R> records;

    BatchReturningResultImpl(int total, @NotNull List<R> rs) {
        super(total, rs.size());
        this.records = rs;
    }

    @NotNull
    @Override
    public List<R> getRecords() { return records; }

}
