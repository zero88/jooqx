package io.github.zero88.jooqx;

class BatchResultImpl implements BatchResult {

    private final int total;
    private final int successes;

    BatchResultImpl(int total, int successes) {
        this.total     = total;
        this.successes = successes;
    }

    @Override
    public int getTotal() { return total; }

    @Override
    public int getSuccesses() { return successes; }

}
