package io.zero88.jooqx.adapter;

import java.util.Collection;

import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.Table;
import org.jooq.TableLike;
import org.jooq.TableRecord;
import org.jooq.impl.DSL;

import io.zero88.jooqx.JsonRecord;
import io.zero88.jooqx.SQLResultCollector;
import io.zero88.jooqx.adapter.SQLCollectorPart.IdentityCollectorPart;
import io.zero88.jooqx.datatype.DataTypeMapperRegistry;

import lombok.NonNull;

/**
 * SQL Result adapter receives Result set then mapping to expected result
 *
 * @param <T> Type of jOOQ Table in Query context
 * @param <O> Type of an expectation output
 * @see TableLike
 * @see Record
 * @see SQLResultCollector
 * @since 1.0.0
 */
public interface SQLResultAdapter<T extends TableLike<? extends Record>, O> extends HasStrategy {

    @SuppressWarnings( {"rawtypes", "unchecked"})
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
    @NonNull T table();

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
    @NonNull <RS> O collect(@NonNull RS resultSet, @NonNull SQLResultCollector<RS> collector, @NonNull DSLContext dsl,
                            @NonNull DataTypeMapperRegistry registry);

    /**
     * Indicates select only one row
     *
     * @since 1.0.0
     */
    interface SelectOneStrategy extends HasStrategy {

        @Override
        default @NonNull SelectStrategy strategy() {
            return SelectStrategy.FIRST_ONE;
        }

    }


    /**
     * Indicates select many row
     *
     * @since 1.0.0
     */
    interface SelectManyStrategy extends HasStrategy {

        @Override
        default @NonNull SelectStrategy strategy() {
            return SelectStrategy.MANY;
        }

    }

}
