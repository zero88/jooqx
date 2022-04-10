package io.github.zero88.jooqx.adapter;

import java.util.Collection;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.Table;
import org.jooq.TableLike;
import org.jooq.TableRecord;
import org.jooq.impl.DSL;

import io.github.zero88.jooqx.JsonRecord;
import io.github.zero88.jooqx.SQLResultCollector;
import io.github.zero88.jooqx.adapter.SQLCollectorPart.IdentityCollectorPart;
import io.github.zero88.jooqx.datatype.DataTypeMapperRegistry;

/**
 * SQL Result adapter receives Result set then mapping to expected result
 *
 * @param <T> Type of jOOQ Table in Query context
 * @param <R> Type of expectation output
 * @see TableLike
 * @see Record
 * @see SQLResultCollector
 * @since 1.0.0
 */
public interface SQLResultAdapter<T, R> extends HasStrategy {

    @SuppressWarnings({"rawtypes", "unchecked"})
    static IdentityCollectorPart<JsonRecord<?>> byJson() {
        return new IdentityCollectorPart<>((dsl, queryTbl) -> JsonRecord.create((TableLike<TableRecord>) queryTbl));
    }

    static <R extends Record> IdentityCollectorPart<R> byRecord(R record) {
        return new IdentityCollectorPart<>((dsl, queryTbl) -> dsl.newRecord(DSL.table(record)));
    }

    static IdentityCollectorPart<Record> byFields(Collection<Field<?>> fields) {
        return new IdentityCollectorPart<>((dsl, queryTbl) -> dsl.newRecord(fields));
    }

    static <R> SQLCollectorPart<JsonRecord<?>, R> byClass(Class<R> outputClass) {
        return byJson().andThen(r -> r.into(outputClass));
    }

    @SuppressWarnings("unchecked")
    static <R extends Record, T extends TableLike<R>> IdentityCollectorPart<R> byTable(T table) {
        return new IdentityCollectorPart<>((dsl, queryTbl) -> table instanceof Table
                                                              ? ((Table<R>) table).newRecord()
                                                              : dsl.newRecord(table.asTable()));
    }

    /**
     * A current context holder
     *
     * @return jOOQ table
     * @see TableLike
     */
    @NotNull T table();

    /**
     * Collect result set to expected result
     *
     * @param resultSet result set
     * @param collector result collector
     * @param dsl       jOOQ DSL context
     * @param registry  SQL data type mapper registry
     * @return an expected result
     * @see DataTypeMapperRegistry
     */
    @NotNull <RS> R collect(@NotNull RS resultSet, @NotNull SQLResultCollector<RS> collector, @NotNull DSLContext dsl,
        @NotNull DataTypeMapperRegistry registry);

    /**
     * Indicates select many row
     *
     * @see SQLResultAdapter
     * @since 1.0.0
     */
    interface SQLResultListAdapter<T, R> extends SQLResultAdapter<T, List<R>> {

        @Override
        @NotNull <RS> List<R> collect(@NotNull RS resultSet, @NotNull SQLResultCollector<RS> collector,
            @NotNull DSLContext dsl, @NotNull DataTypeMapperRegistry registry);

        @Override
        default @NotNull SelectStrategy strategy() {
            return SelectStrategy.MANY;
        }

    }


    /**
     * Indicates select only one row
     *
     * @since 1.0.0
     */
    interface SQLResultOneAdapter<T, R> extends SQLResultAdapter<T, R> {

        @Override
        default @NotNull SelectStrategy strategy() {
            return SelectStrategy.FIRST_ONE;
        }

        @Override
        @NotNull <RS> R collect(@NotNull RS resultSet, @NotNull SQLResultCollector<RS> collector,
            @NotNull DSLContext dsl, @NotNull DataTypeMapperRegistry registry);

    }

}
