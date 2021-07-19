package io.zero88.jooqx.provider;

import org.jetbrains.annotations.NonNls;

interface HasSQLClient<S> {

    /**
     * SQL client
     *
     * @return SQL client
     */
    @NonNls S sqlClient();

}
