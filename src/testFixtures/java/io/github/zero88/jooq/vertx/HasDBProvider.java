package io.github.zero88.jooq.vertx;

public interface HasDBProvider<K, D extends DBProvider<K>> {

    D dbProvider();

}
