package io.github.zero88.jooq.vertx;

import org.jooq.Query;
import org.jooq.TableLike;

import io.github.zero88.jooq.vertx.converter.BindBatchValues;
import io.github.zero88.jooq.vertx.converter.ResultSetBatchConverter;
import io.github.zero88.jooq.vertx.converter.ResultSetConverter;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;

import lombok.NonNull;

/**
 * @param <RS> Type of result set
 * @param <BR> Type of batch result
 */
public interface VertxBatchExecutor<RS, BR> {

    /**
     * Execute {@code jOOQ query} with batch records then return async result
     *
     * @param <Q>             type of jOOQ Query
     * @param <T>             type of jOOQ TableLike
     * @param query           jOOQ query
     * @param rsConverter     a result set converter
     * @param bindBatchValues a batch params
     * @param handler         a async result handler
     * @see TableLike
     * @see Query
     * @see ResultSetConverter#convert(Object)
     * @see BindBatchValues
     */
    <Q extends Query, T extends TableLike<?>> void batchExecute(@NonNull Q query,
                                                                @NonNull ResultSetBatchConverter<RS, T> rsConverter,
                                                                @NonNull BindBatchValues bindBatchValues,
                                                                @NonNull Handler<AsyncResult<BR>> handler);

}
