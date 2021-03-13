package io.zero88.jooqx.adapter;

import org.jooq.Record;
import org.jooq.TableLike;

import io.zero88.jooqx.SQLResultCollector;
import io.zero88.jooqx.datatype.SQLDataTypeRegistry;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

/**
 * Select Adhoc result adapter is a base class for custom {@code Select} implementations in client code.
 *
 * @see SQLResultAdapter
 */
@Getter
@Accessors(fluent = true)
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class SelectAdhocResultAdapter<RS, C extends SQLResultCollector<RS>, T extends TableLike<? extends Record>, O>
    implements SQLResultAdapter<RS, C, T, O> {

    @NonNull
    private final T table;
    @NonNull
    private final C converter;

    protected final RowConverterStrategyImpl converterStrategy(@NonNull SQLDataTypeRegistry registry) {
        return new RowConverterStrategyImpl(strategy(), registry, table());
    }

}
