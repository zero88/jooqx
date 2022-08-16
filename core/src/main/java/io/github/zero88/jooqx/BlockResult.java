package io.github.zero88.jooqx;

import java.util.List;

/**
 * A block result
 *
 * @since 2.0.0
 */
public interface BlockResult {

    static BlockResult create() {
        return new BlockResultImpl();
    }

    int size();

    void append(Object result);

    <R> R get(int index);

    List<Object> results();

}
