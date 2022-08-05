package io.github.zero88.jooqx;

import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jooq.DSLContext;

import io.github.zero88.jooqx.LegacySQLImpl.LegacySQLRC;
import io.github.zero88.jooqx.adapter.SQLResultAdapter;
import io.github.zero88.jooqx.datatype.DataTypeMapperRegistry;
import io.vertx.codegen.annotations.GenIgnore;
import io.vertx.codegen.annotations.Nullable;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.ext.sql.ResultSet;

/**
 * Represents for Legacy SQL result set collector
 *
 * @since 1.0.0
 */
@VertxGen
@Deprecated
public interface LegacySQLCollector extends SQLResultCollector<ResultSet>, SQLBatchCollector<ResultSet, List<Integer>> {

    static LegacySQLCollector create() {
        return new LegacySQLRC();
    }

    @Override
    int batchResultSize(@NotNull List<Integer> batchResult);

    @Override
    @GenIgnore(GenIgnore.PERMITTED_TYPE)
    <ROW, RESULT> @Nullable RESULT collect(@NotNull ResultSet resultSet, @NotNull SQLResultAdapter<ROW, RESULT> adapter,
                                           @NotNull DSLContext dslContext, @NotNull DataTypeMapperRegistry registry);

}
