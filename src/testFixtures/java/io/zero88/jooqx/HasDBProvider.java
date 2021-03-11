package io.zero88.jooqx;

public interface HasDBProvider<K, D extends DBProvider<K>> {

    D dbProvider();

}
