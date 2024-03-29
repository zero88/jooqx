package io.github.zero88.jooqx.provider;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.jdbc.spi.DataSourceProvider;
import io.vertx.ext.jdbc.spi.impl.AgroalCPDataSourceProvider;
import io.vertx.ext.jdbc.spi.impl.C3P0DataSourceProvider;
import io.vertx.ext.jdbc.spi.impl.HikariCPDataSourceProvider;

/**
 * Extension for third-party JDBC connection pool library
 *
 * @param <P> Type of DataSource provider
 * @see DataSourceProvider
 */
public interface JDBCExtension<P extends DataSourceProvider> {

    /**
     * DataSource provider class
     *
     * @return Data Source provider class
     */
    Class<P> dataSourceProviderClass();

    /**
     * Defines JDBC data source class
     * <p>
     * It helps for detecting and scanning in runtime
     *
     * @return JDBC data source class
     */
    String jdbcDataSourceClass();

    /**
     * Optimize Data Source provider config
     *
     * @param config config
     * @return SQL connection config
     */
    default JsonObject optimizeDataSourceProviderConfig(JsonObject config) {
        return config.put("provider_class", dataSourceProviderClass().getName());
    }

    /**
     * HikariCP extension
     *
     * @see HikariCPDataSourceProvider
     * @since 2.0.0
     */
    interface HikariCPExtension extends JDBCExtension<HikariCPDataSourceProvider> {

        @Override
        default String jdbcDataSourceClass() {
            return "com.zaxxer.hikari.HikariDataSource";
        }

        @Override
        default Class<HikariCPDataSourceProvider> dataSourceProviderClass() {
            return HikariCPDataSourceProvider.class;
        }

        @Override
        default JsonObject optimizeDataSourceProviderConfig(JsonObject config) {
            if (config.getString("jdbcUrl") == null) {
                config.put("jdbcUrl", config.getString("url"));
                config.put("url", config.getString("jdbcUrl"));
            }
            if (config.getString("username") == null) {
                config.put("username", config.getString("user"));
                config.put("user", config.getString("username"));
            }
            if (config.getString("driverClassName") == null) {
                config.put("driverClassName", config.getString("driver_class"));
                config.put("driver_class", config.getString("driverClassName"));
            }
            return JDBCExtension.super.optimizeDataSourceProviderConfig(config);
        }

    }

    /**
     * C3P0 extension
     *
     * @see C3P0DataSourceProvider
     * @since 2.0.0
     */
    interface C3P0Extension extends JDBCExtension<C3P0DataSourceProvider> {

        @Override
        default String jdbcDataSourceClass() {
            return "com.mchange.v2.c3p0.PooledDataSource";
        }

        @Override
        default Class<C3P0DataSourceProvider> dataSourceProviderClass() {
            return C3P0DataSourceProvider.class;
        }

    }

    /**
     * Agroal extension
     *
     * @see AgroalCPDataSourceProvider
     * @since 2.0.0
     */
    interface AgroalExtension extends JDBCExtension<AgroalCPDataSourceProvider> {

        @Override
        default String jdbcDataSourceClass() {
            return "io.agroal.api.AgroalDataSource";
        }

        @Override
        default Class<AgroalCPDataSourceProvider> dataSourceProviderClass() {
            return AgroalCPDataSourceProvider.class;
        }

    }

}
