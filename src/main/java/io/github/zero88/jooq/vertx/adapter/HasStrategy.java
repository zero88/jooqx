package io.github.zero88.jooq.vertx.adapter;

import lombok.NonNull;

public interface HasStrategy {

    /**
     * Strategy in handling a result set converting process
     *
     * @return select strategy
     */
    @NonNull SelectStrategy strategy();

    interface SelectOne extends HasStrategy {

        @Override
        default @NonNull SelectStrategy strategy() {
            return SelectStrategy.FIRST_ONE;
        }

    }


    interface SelectMany extends HasStrategy {

        @Override
        default @NonNull SelectStrategy strategy() {
            return SelectStrategy.MANY;
        }

    }

}
