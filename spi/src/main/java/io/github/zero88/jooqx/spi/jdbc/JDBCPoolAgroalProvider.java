package io.github.zero88.jooqx.spi.jdbc;

import io.vertx.jdbcclient.JDBCPool;
import io.vertx.jdbcclient.impl.AgroalCPDataSourceProvider;

/**
 * Reactive JDBC pool from Vert.x official that using {@code AgroalCP}
 *
 * @see JDBCPool
 * @since 2.0.0
 */
public interface JDBCPoolAgroalProvider extends JDBCPoolProvider<AgroalCPDataSourceProvider> {

    @Override
    default String jdbcDataSourceClass() {
        return "io.agroal.api.AgroalDataSource";
    }

    @Override
    default Class<AgroalCPDataSourceProvider> dataSourceProviderClass() {
        return AgroalCPDataSourceProvider.class;
    }

}
