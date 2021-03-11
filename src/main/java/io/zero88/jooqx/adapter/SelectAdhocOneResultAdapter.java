package io.zero88.jooqx.adapter;

import java.util.function.BiFunction;

import org.jooq.Record;
import org.jooq.TableLike;

import io.zero88.jooqx.SQLResultSetConverter;
import io.zero88.jooqx.adapter.HasStrategy.SelectOne;

import lombok.NonNull;

/**
 * Select Adhoc for one result adapter
 *
 * @param <R> Type of Vertx Result set
 * @param <C> Type of result set converter
 * @param <T> Type of jOOQ Table
 * @param <O> Type of an expectation output
 * @see SQLResultAdapterImpl
 * @see SelectOne
 * @since 1.0.0
 */
public abstract class SelectAdhocOneResultAdapter<R, C extends SQLResultSetConverter<R>, T extends TableLike<? extends Record>, O>
    extends SQLResultAdapterImpl<R, C, T, O> implements SelectOne {

    private final BiFunction<SQLResultAdapter<R, C, T, O>, R, O> function;

    protected SelectAdhocOneResultAdapter(@NonNull T table, @NonNull C converter,
                                          BiFunction<SQLResultAdapter<R, C, T, O>, R, O> function) {
        super(table, converter);
        this.function = function;
    }

    @Override
    public @NonNull O convert(@NonNull R resultSet) {
        return function.apply(this, resultSet);
    }

}
