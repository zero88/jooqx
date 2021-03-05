package io.github.zero88.jooq.vertx.converter;

import org.jooq.Record;
import org.jooq.TableLike;

public interface ResultSetBatchConverter<RS, T extends TableLike<? extends Record>> extends ResultSetConverter<RS, T> {}
