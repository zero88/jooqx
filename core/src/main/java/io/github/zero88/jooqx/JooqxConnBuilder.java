package io.github.zero88.jooqx;

import org.jetbrains.annotations.NotNull;
import org.jooq.DSLContext;

import io.github.zero88.jooqx.datatype.DataTypeMapperRegistry;
import io.vertx.codegen.annotations.Fluent;
import io.vertx.codegen.annotations.GenIgnore;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.Vertx;
import io.vertx.sqlclient.SqlConnection;
import io.vertx.sqlclient.Tuple;

/**
 * Represents a builder that constructs {@link JooqxConn}
 *
 * @since 2.0.0
 */
@VertxGen
public interface JooqxConnBuilder
    extends SQLExecutorBuilder<SqlConnection, Tuple, JooqxPreparedQuery, JooqxResultCollector, JooqxConnBuilder> {

    @NotNull Vertx vertx();

    @Fluent
    @NotNull JooqxConnBuilder setVertx(Vertx vertx);

    @GenIgnore(GenIgnore.PERMITTED_TYPE)
    @NotNull DSLContext dsl();

    @Fluent
    @GenIgnore(GenIgnore.PERMITTED_TYPE)
    @NotNull JooqxConnBuilder setDSL(DSLContext dsl);

    @NotNull SqlConnection sqlClient();

    @Fluent
    @NotNull JooqxConnBuilder setSqlClient(SqlConnection sqlClient);

    JooqxPreparedQuery preparedQuery();

    @Fluent
    @NotNull JooqxConnBuilder setPreparedQuery(JooqxPreparedQuery preparedQuery);

    JooqxResultCollector resultCollector();

    @Fluent
    @NotNull JooqxConnBuilder setResultCollector(JooqxResultCollector resultCollector);

    @GenIgnore(GenIgnore.PERMITTED_TYPE)
    SQLErrorConverter errorConverter();

    @Fluent
    @GenIgnore(GenIgnore.PERMITTED_TYPE)
    @NotNull JooqxConnBuilder setErrorConverter(SQLErrorConverter errorConverter);

    @GenIgnore(GenIgnore.PERMITTED_TYPE)
    DataTypeMapperRegistry typeMapperRegistry();

    @Fluent
    @GenIgnore(GenIgnore.PERMITTED_TYPE)
    @NotNull JooqxConnBuilder setTypeMapperRegistry(DataTypeMapperRegistry typeMapperRegistry);

    /**
     * @return a new {@link JooqxConn} instance
     */
    @NotNull JooqxConn build();

}
