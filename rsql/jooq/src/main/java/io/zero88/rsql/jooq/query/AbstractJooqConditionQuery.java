package io.zero88.rsql.jooq.query;

import org.jetbrains.annotations.NotNull;
import org.jooq.Condition;
import org.jooq.Query;
import org.jooq.TableLike;

import io.zero88.rsql.jooq.visitor.DefaultJooqConditionRqlVisitor;
import io.zero88.rsql.jooq.visitor.JooqConditionRqlVisitor;

import lombok.experimental.SuperBuilder;

@SuperBuilder
public abstract class AbstractJooqConditionQuery<R> extends AbstractJooqQuery<R, Condition, Void>
    implements JooqConditionQuery<R> {

    @NotNull
    private final TableLike table;

    public @NotNull TableLike table() {return this.table;}

    @Override
    public @NotNull JooqConditionRqlVisitor visitor() {
        return DefaultJooqConditionRqlVisitor.builder()
                                             .table(table())
                                             .queryContext(queryContext())
                                             .criteriaBuilderFactory(criteriaBuilderFactory())
                                             .build();
    }

    public @NotNull R execute(@NotNull String query) {
        return execute(parser().criteria(query, visitor()));
    }

    @Override
    public @NotNull Query toQuery(@NotNull String query) {
        return toQuery(parser().criteria(query, visitor()));
    }

}
