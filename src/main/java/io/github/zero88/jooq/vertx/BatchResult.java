package io.github.zero88.jooq.vertx;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class BatchResult {

    private final int total;
    private final int successes;

}
