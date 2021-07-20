package io.zero88.jooqx;

import io.zero88.jooqx.provider.DBProvider;

/**
 * @param <DB>  Type of database
 * @param <DBP> Type of database provider
 */
public interface HasDBProvider<DB, DBP extends DBProvider<DB>> {

    DBP dbProvider();

}
