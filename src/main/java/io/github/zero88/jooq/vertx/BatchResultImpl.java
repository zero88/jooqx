package io.github.zero88.jooq.vertx;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
class BatchResultImpl implements BatchResult {

    private final int total;
    private final int successes;

    static BatchResult create(int total, int successes) {
        return new BatchResultImpl(total, successes);
    }

    static <R> BatchReturningResult<R> create(int total, List<R> results) {
        return new BatchReturningResultImpl<>(total, Optional.ofNullable(results).orElseGet(ArrayList::new));
    }

    private static final class BatchReturningResultImpl<R> extends BatchResultImpl implements BatchReturningResult<R> {

        @Getter
        @NonNull
        private final List<R> records;

        public BatchReturningResultImpl(int total, @NonNull List<R> rs) {
            super(total, rs.size());
            this.records = rs;
        }

    }

}
