package io.github.zero88.jooqx;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

import org.jetbrains.annotations.NotNull;
import org.jooq.Query;

import io.github.zero88.jooqx.adapter.SQLResultAdapter;

class BlockQueryImpl implements BlockQuery {

    private final boolean inBlock;
    private final List<Query> queries = new ArrayList<>();
    private final List<SQLResultAdapter> adapters = new ArrayList<>();

    BlockQueryImpl(boolean isInBlock) {
        this(new ArrayList<>(), new ArrayList<>(), isInBlock);
    }

    BlockQueryImpl(List<Query> queries, List<SQLResultAdapter> adapters, boolean inBlock) {
        this.inBlock = inBlock;
        if (queries.size() != adapters.size()) {
            throw new IllegalArgumentException("The size of queries and adapters is not compatible");
        }
        IntStream.range(0, queries.size()).forEach(idx -> add(queries.get(idx), adapters.get(idx)));
    }

    @Override
    public boolean isInBlock() {
        return inBlock;
    }

    @Override
    public @NotNull List<Query> queries() {
        return queries;
    }

    @Override
    public @NotNull List<SQLResultAdapter> adapters() {
        return adapters;
    }

    @Override
    public @NotNull BlockQuery add(Query query, SQLResultAdapter adapter) {
        this.queries.add(Objects.requireNonNull(query, "Query must be not null"));
        this.adapters.add(Objects.requireNonNull(adapter, "Result adapter must be not null"));
        return this;
    }

}
