package io.zero88.rsql.jooq.query;

import java.util.Collections;
import java.util.Objects;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
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

/**
 * Represents for jOOQ paging and sorting query.
 *
 * @see SelectOptionStep
 * @see JooqConditionQuery
 * @since 1.0.0
 */
public final class JooqPagingAndSortingQuery
    extends AbstractJooqConditionQuery<SelectOptionStep<Record>, SelectOptionStep<Record>> {

    private final Pageable pageable;
    private final Sortable sortable;

    private JooqPagingAndSortingQuery(JooqPagingAndSortingQueryBuilder b) {
        super(b);
        this.pageable = b.pageable;
        this.sortable = b.sortable;
    }

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

    private SelectSeekStepN<Record> orderBy(@NotNull SelectConditionStep<Record> sql, @Nullable Sortable sort) {
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

    private @Nullable OrderField<?> sortField(@NotNull Order order) {
        return context().queryContext()
                        .fieldMapper()
                        .get(context().subject(), order.property())
                        .map(f -> order.direction().isASC() ? f.asc() : f.desc())
                        .orElse(null);
    }

    private SelectOptionStep<Record> paging(@NotNull SelectLimitStep<Record> sql, @NotNull Pageable pagination) {
        return sql.limit(pagination.getPageSize()).offset((pagination.getPage() - 1) * pagination.getPageSize());
    }

    public static JooqPagingAndSortingQueryBuilder builder() {return new JooqPagingAndSortingQueryBuilder();}

    //@formatter:off
    public static final class JooqPagingAndSortingQueryBuilder extends
                                                               AbstractJooqConditionQueryBuilder<SelectOptionStep<Record>,
                                                                                                    SelectOptionStep<Record>,
                                                                                                    JooqPagingAndSortingQuery,
                                                                                                    JooqPagingAndSortingQueryBuilder> {
    //@formatter:on

        private Pageable pageable;
        private Sortable sortable;

        public JooqPagingAndSortingQueryBuilder pageable(Pageable pageable) {
            this.pageable = pageable;
            return self();
        }

        public JooqPagingAndSortingQueryBuilder sortable(Sortable sortable) {
            this.sortable = sortable;
            return self();
        }

        protected JooqPagingAndSortingQueryBuilder self() {return this;}

        public JooqPagingAndSortingQuery build()          {return new JooqPagingAndSortingQuery(this);}

    }

}
