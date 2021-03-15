package io.zero88.jooqx.adapter;

import lombok.NonNull;

interface HasStrategy {

    /**
     * Select strategy for handling a result set
     *
     * @return select strategy
     */
    @NonNull SelectStrategy strategy();

}
