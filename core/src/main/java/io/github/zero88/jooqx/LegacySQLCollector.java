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
public interface LegacySQLCollector extends SQLResultCollector, SQLBatchCollector<List<Integer>> {

    static LegacySQLCollector create() {
        return new LegacySQLRC();
    }

    /**
     * Collect result set to an expectation result that defines in SQL result adapter
     *
     * @param <ROW>      the type of jOOQ record of the reduction operation
     * @param <RESULT>   the type of result after the reduction operation
     * @param resultSet  the result set
     * @param adapter    the SQL result adapter
     * @param dslContext the DSL context
     * @param registry   the data type registry
     * @return an expectation result
     * @see SQLResultAdapter
     * @see DataTypeMapperRegistry
     * @since 2.0.0
     */
    @GenIgnore(GenIgnore.PERMITTED_TYPE)
    <ROW, RESULT> @Nullable RESULT collect(@NotNull ResultSet resultSet, @NotNull SQLResultAdapter<ROW, RESULT> adapter,
                                           @NotNull DSLContext dslContext, @NotNull DataTypeMapperRegistry registry);

    @Override
    default int batchResultSize(@NotNull List<Integer> batchResult) {
        return batchResult.size();
    }

    /**
     * Collect multiple result set to block result
     *
     * @param multipleResultSet multiple result set
     * @param adapters          the adapters
     * @param dslContext        the DSL context
     * @param registry          the data type registry
     * @return a Block result
     * @see BlockResult
     */
    @GenIgnore(GenIgnore.PERMITTED_TYPE)
    default BlockResult collect(@NotNull ResultSet multipleResultSet, @NotNull List<SQLResultAdapter> adapters,
                                @NotNull DSLContext dslContext, @NotNull DataTypeMapperRegistry registry) {
        ResultSet rs = multipleResultSet;
        final BlockResult blockResult = BlockResult.create();
        final int[] count = { 0 };
        do {
            blockResult.append(collect(rs, adapters.get(count[0]), dslContext, registry));
            count[0]++;
        } while ((rs = rs.getNext()) != null);
        return blockResult;
    }

}
