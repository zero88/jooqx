package io.github.zero88.jooq.vertx.converter;

import lombok.NonNull;

/**
 * Result batch converter
 *
 * @param <RS> Type of Vertx SQL result set
 * @param <BR> Type of Vertx SQL batch result
 * @see ResultSetConverter
 */
public interface ResultBatchConverter<RS, BR> extends ResultSetConverter<RS> {

    int count(@NonNull BR batchResult);

}
