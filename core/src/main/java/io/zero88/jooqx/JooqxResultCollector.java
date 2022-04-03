package io.zero88.jooqx;

import java.util.List;

import org.jetbrains.annotations.NotNull;

import io.vertx.codegen.annotations.GenIgnore;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import io.zero88.jooqx.JooqxSQLImpl.ReactiveSQLRC;
import io.zero88.jooqx.adapter.RowConverterStrategy;

/**
 * Reactive result set converter
 *
 * @since 1.1.0
 */
@VertxGen
public interface JooqxResultCollector extends SQLResultCollector<RowSet<Row>> {

    static JooqxResultCollector create() {
        return new ReactiveSQLRC();
    }

    @Override
    @GenIgnore(GenIgnore.PERMITTED_TYPE)
    @NotNull <T, R> List<R> collect(@NotNull RowSet<Row> resultSet, @NotNull RowConverterStrategy<T, R> strategy);

}
