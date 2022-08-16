package io.github.zero88.jooqx;

import java.util.ArrayList;
import java.util.List;

class BlockResultImpl implements BlockResult {

    private final List<Object> results = new ArrayList<>();

    @Override
    public int size() {
        return results.size();
    }

    @Override
    public void append(Object result) {
        results.add(result);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <R> R get(int index) {
        return (R) results.get(index);
    }

    @Override
    public List<Object> results() {
        return results;
    }

}
