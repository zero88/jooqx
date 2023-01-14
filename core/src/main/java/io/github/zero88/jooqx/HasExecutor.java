package io.github.zero88.jooqx;

import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
public interface HasExecutor {

    @Internal
    SQLStatementExecutor executor();

}
