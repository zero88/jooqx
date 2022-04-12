package io.github.zero88.jooqx;

import org.jetbrains.annotations.NotNull;
import org.jooq.DSLContext;

import io.github.zero88.jooqx.datatype.DataTypeMapperRegistry;
import io.vertx.codegen.annotations.Fluent;
import io.vertx.codegen.annotations.GenIgnore;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.sql.ResultSet;
import io.vertx.ext.sql.SQLClient;

/**
 * Represents a builder that constructs {@link LegacyJooqx}
 *
 * @since 2.0.0
 */
@VertxGen
@Deprecated
public interface LegacyJooqxBuilder extends
                                    SQLExecutorBuilder<SQLClient, JsonArray, LegacySQLPreparedQuery, ResultSet,
                                                          LegacySQLCollector, LegacyJooqxBuilder> {

    @Override
    @NotNull Vertx vertx();

    @Fluent
    @NotNull LegacyJooqxBuilder setVertx(Vertx vertx);

    @GenIgnore(GenIgnore.PERMITTED_TYPE)
    @NotNull DSLContext dsl();

    @Fluent
    @GenIgnore(GenIgnore.PERMITTED_TYPE)
    @NotNull LegacyJooqxBuilder setDSL(DSLContext dsl);

    /**
     * @see SQLClient
     */
    @NotNull SQLClient sqlClient();

    /**
     * @see SQLClient
     */
    @Fluent
    @NotNull LegacyJooqxBuilder setSqlClient(SQLClient sqlClient);

    /**
     * @see LegacySQLPreparedQuery
     */
    LegacySQLPreparedQuery preparedQuery();

    /**
     * @see LegacySQLPreparedQuery
     */
    @Fluent
    @NotNull LegacyJooqxBuilder setPreparedQuery(LegacySQLPreparedQuery preparedQuery);

    /**
     * @see LegacySQLCollector
     */
    LegacySQLCollector resultCollector();

    /**
     * @see LegacySQLCollector
     */
    @Fluent
    @NotNull LegacyJooqxBuilder setResultCollector(LegacySQLCollector resultCollector);

    @GenIgnore(GenIgnore.PERMITTED_TYPE)
    SQLErrorConverter errorConverter();

    @Fluent
    @GenIgnore(GenIgnore.PERMITTED_TYPE)
    @NotNull LegacyJooqxBuilder setErrorConverter(SQLErrorConverter errorConverter);

    @GenIgnore(GenIgnore.PERMITTED_TYPE)
    DataTypeMapperRegistry typeMapperRegistry();

    @Fluent
    @GenIgnore(GenIgnore.PERMITTED_TYPE)
    @NotNull LegacyJooqxBuilder setTypeMapperRegistry(DataTypeMapperRegistry typeMapperRegistry);

    /**
     * @return a new {@link LegacyJooqx} instance
     */
    @NotNull LegacyJooqx build();

}
