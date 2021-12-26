package io.zero88.rsql.jooq.query;

import org.jooq.Condition;
import org.jooq.Query;
import org.jooq.TableLike;

import io.zero88.rsql.jooq.visitor.DefaultJooqConditionRqlVisitor;
import io.zero88.rsql.jooq.visitor.JooqConditionRqlVisitor;

import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@Accessors(fluent = true)
public abstract class AbstractJooqConditionQuery<R> extends AbstractJooqQuery<R, Condition, Void>
    implements JooqConditionQuery<R> {

    @NonNull
    private final TableLike table;

    @Override
    public @NonNull JooqConditionRqlVisitor visitor() {
        return DefaultJooqConditionRqlVisitor.builder()
                                             .table(table())
                                             .queryContext(queryContext())
                                             .criteriaBuilderFactory(criteriaBuilderFactory())
                                             .build();
    }

    public @NonNull R execute(@NonNull String query) {
        return execute(parser().criteria(query, visitor()));
    }

    @Override
    public @NonNull Query toQuery(@NonNull String query) {
        return toQuery(parser().criteria(query, visitor()));
    }

}
