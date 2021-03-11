package io.zero88.jooqx;

import lombok.NonNull;

/**
 * Result batch converter
 *
 * @param <RS> Type of Vertx SQL result set
 * @param <BR> Type of Vertx SQL batch result
 * @see SQLResultSetConverter
 */
public interface SQLResultBatchConverter<RS, BR> extends SQLResultSetConverter<RS> {

    int batchResultSize(@NonNull BR batchResult);

}
