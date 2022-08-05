package io.github.zero88.jooqx;

import org.jetbrains.annotations.NotNull;
import org.jooq.DSLContext;

import io.github.zero88.jooqx.JooqxSQLImpl.ReactiveSQLRC;
import io.github.zero88.jooqx.adapter.SQLResultAdapter;
import io.github.zero88.jooqx.datatype.DataTypeMapperRegistry;
import io.vertx.codegen.annotations.GenIgnore;
import io.vertx.codegen.annotations.Nullable;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;

/**
 * Represents for a collector that collects then transform {@code Vert.x SQL result} to an expectation output
 *
 * @since 2.0.0
 */
@VertxGen
public interface JooqxResultCollector extends SQLResultCollector<RowSet<Row>> {

    static JooqxResultCollector create() {
        return new ReactiveSQLRC();
    }

    @Override
    @GenIgnore(GenIgnore.PERMITTED_TYPE)
    <ROW, RESULT> @Nullable RESULT collect(@NotNull RowSet<Row> resultSet,
                                           @NotNull SQLResultAdapter<ROW, RESULT> adapter,
                                           @NotNull DSLContext dslContext, @NotNull DataTypeMapperRegistry registry);

}
