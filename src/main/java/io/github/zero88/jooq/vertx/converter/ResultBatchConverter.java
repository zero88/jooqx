package io.github.zero88.jooq.vertx.converter;

import org.jooq.Record;
import org.jooq.TableLike;

public interface ResultBatchConverter<RS, T extends TableLike<? extends Record>> extends ResultSetConverter<RS, T> {}
