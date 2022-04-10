package io.github.zero88.jooqx;

import org.jetbrains.annotations.NotNull;
import org.jooq.DSLContext;

import io.github.zero88.jooqx.datatype.DataTypeMapperRegistry;
import io.vertx.codegen.annotations.Fluent;
import io.vertx.codegen.annotations.GenIgnore;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.Vertx;
import io.vertx.sqlclient.Pool;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import io.vertx.sqlclient.Tuple;

/**
 * Represents a builder that construct {@link Jooqx}
 *
 * @since 2.0.0
 */
@VertxGen
public interface JooqxBuilder
    extends SQLExecutorBuilder<Pool, Tuple, JooqxPreparedQuery, RowSet<Row>, JooqxResultCollector, JooqxBuilder> {

    @NotNull Vertx vertx();

    @Fluent
    @NotNull JooqxBuilder setVertx(Vertx vertx);

    @GenIgnore(GenIgnore.PERMITTED_TYPE)
    @NotNull DSLContext dsl();

    @Fluent
    @GenIgnore(GenIgnore.PERMITTED_TYPE)
    @NotNull JooqxBuilder setDSL(DSLContext dsl);

    /**
     * @see Pool
     */
    @NotNull Pool sqlClient();

    /**
     * @see Pool
     */
    @Fluent
    @NotNull JooqxBuilder setSqlClient(Pool sqlClient);

    /**
     * @see JooqxPreparedQuery
     */
    JooqxPreparedQuery preparedQuery();

    /**
     * @see JooqxPreparedQuery
     */
    @Fluent
    @NotNull JooqxBuilder setPreparedQuery(JooqxPreparedQuery preparedQuery);

    /**
     * @see JooqxResultCollector
     */
    JooqxResultCollector resultCollector();

    /**
     * @see JooqxResultCollector
     */
    @Fluent
    @NotNull JooqxBuilder setResultCollector(JooqxResultCollector resultCollector);

    @GenIgnore(GenIgnore.PERMITTED_TYPE)
    SQLErrorConverter errorConverter();

    @Fluent
    @GenIgnore(GenIgnore.PERMITTED_TYPE)
    @NotNull JooqxBuilder setErrorConverter(SQLErrorConverter errorConverter);

    @GenIgnore(GenIgnore.PERMITTED_TYPE)
    DataTypeMapperRegistry typeMapperRegistry();

    @Fluent
    @GenIgnore(GenIgnore.PERMITTED_TYPE)
    @NotNull JooqxBuilder setTypeMapperRegistry(DataTypeMapperRegistry typeMapperRegistry);

    /**
     * @return a new {@link Jooqx} instance
     */
    @NotNull Jooqx build();

}
