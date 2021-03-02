package io.github.zero88.jooq.vertx.converter;

import java.util.List;

import org.jooq.Record;
import org.jooq.Table;

import lombok.NonNull;

public interface ResultSetConverter<RS, REC extends Record, TABLE extends Table<REC>> {

    TABLE table();

    List<REC> convert(@NonNull RS resultSet);

}
