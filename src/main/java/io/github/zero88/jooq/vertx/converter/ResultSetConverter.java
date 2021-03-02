package io.github.zero88.jooq.vertx.converter;

import java.util.List;

import org.jooq.Field;
import org.jooq.Record;
import org.jooq.TableLike;

import io.github.zero88.jooq.vertx.record.VertxJooqRecord;

import lombok.NonNull;

/**
 * Represents for a converter that transforms SQL result set to jOOQ record
 *
 * @param <RS> Type of SQL result set
 * @param <T>  Type of Jooq Table Like
 * @see Record
 * @see TableLike
 */
public interface ResultSetConverter<RS, T extends TableLike<? extends Record>> {

    T table();

    Field fieldMapper(String field);

    /**
     * Convert SQL result set to generic {@code Vertx jOOQ record}
     *
     * @param resultSet SQL result set
     * @return list of {@code Vertx jOOQ record}
     * @see VertxJooqRecord
     */
    List<VertxJooqRecord<?>> convert(@NonNull RS resultSet);

    /**
     * Convert result set to an expectation record
     *
     * @param <REC>     Type of record
     * @param resultSet SQL result set
     * @param record    record class
     * @return list of  {@code Vertx jOOQ record}
     */
    default <REC extends Record> List<REC> convert(@NonNull RS resultSet, @NonNull REC record) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

}
