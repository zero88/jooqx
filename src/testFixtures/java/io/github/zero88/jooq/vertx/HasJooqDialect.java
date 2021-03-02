package io.github.zero88.jooq.vertx;

import org.jooq.SQLDialect;

import lombok.NonNull;

public interface HasJooqDialect {

    @NonNull SQLDialect dialect();

}
