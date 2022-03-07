package io.zero88.rsql.jooq.query;

import java.util.Collections;
import java.util.Objects;

import org.jetbrains.annotations.NotNull;
import org.jooq.Condition;
import org.jooq.OrderField;
import org.jooq.Record;
import org.jooq.SelectConditionStep;
import org.jooq.SelectLimitStep;
import org.jooq.SelectOptionStep;
import org.jooq.SelectSeekStepN;

import io.zero88.jpa.Order;
import io.zero88.jpa.Pageable;
import io.zero88.jpa.Sortable;

import lombok.experimental.SuperBuilder;

/**
 * Represents for jOOQ paging and sorting query.
 *
 * @see SelectOptionStep
 * @see JooqConditionQuery
 * @since 1.0.0
 */
@SuperBuilder
public final class JooqPagingAndSortingQuery extends AbstractJooqConditionQuery<SelectOptionStep<Record>> {

    private final Pageable pageable;
    private final Sortable sortable;

    @Override
    public @NotNull SelectOptionStep<Record> execute(@NotNull Condition condition) {
        return paging(orderBy(context().dsl()
                                       .select(context().queryContext().fieldSelector().get())
                                       .from(context().subject())
                                       .where(condition), sortable), pageable);
    }

    @Override
    public @NotNull SelectOptionStep<Record> toQuery(@NotNull Condition condition) {
        return execute(condition);
    }

    private SelectSeekStepN<Record> orderBy(@NotNull SelectConditionStep<Record> sql, Sortable sort) {
        if (Objects.isNull(sort) || sort.isEmpty()) {
            return sql.orderBy(Collections.emptyList());
        }
        return sql.orderBy(sort.orders()
                               .stream()
                               .filter(Objects::nonNull)
                               .filter(order -> !order.property().contains("."))
                               .map(this::sortField)
                               .filter(Objects::nonNull)
                               .toArray(OrderField[]::new));
    }

    private OrderField<?> sortField(@NotNull Order order) {
        return context().queryContext()
                        .fieldMapper()
                        .get(context().subject(), order.property())
                        .map(f -> order.direction().isASC() ? f.asc() : f.desc())
                        .orElse(null);
    }

    private SelectOptionStep<Record> paging(@NotNull SelectLimitStep<Record> sql, @NotNull Pageable pagination) {
        return sql.limit(pagination.getPageSize()).offset((pagination.getPage() - 1) * pagination.getPageSize());
    }

}
