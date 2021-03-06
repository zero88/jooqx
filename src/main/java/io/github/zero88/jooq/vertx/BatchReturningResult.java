package io.github.zero88.jooq.vertx;

import java.util.List;

import lombok.Getter;
import lombok.NonNull;

@Getter
public final class BatchReturningResult<R> extends BatchResult {

    @NonNull
    private final List<R> records;

    public BatchReturningResult(int total, @NonNull List<R> rs) {
        super(total, rs.size());
        this.records = rs;
    }

}
