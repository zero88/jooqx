package io.github.zero88.jooqx.adapter;

import org.jetbrains.annotations.NotNull;

interface HasStrategy {

    /**
     * Select strategy for handling a result set
     *
     * @return select strategy
     */
    @NotNull SelectStrategy strategy();

}
