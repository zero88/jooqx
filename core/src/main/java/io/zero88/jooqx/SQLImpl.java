package io.zero88.jooqx;

import java.sql.SQLNonTransientConnectionException;
import java.sql.SQLTransientConnectionException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.jetbrains.annotations.NotNull;
import org.jooq.Configuration;
import org.jooq.DSLContext;
import org.jooq.Param;
import org.jooq.Query;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.exception.SQLStateClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.Vertx;
import io.zero88.jooqx.datatype.DataTypeMapperRegistry;

final class SQLImpl {

    abstract static class SQLEI<S, B, PQ extends SQLPreparedQuery<B>, RS, RC extends SQLResultCollector<RS>>
        implements SQLExecutor<S, B, PQ, RS, RC> {

        private final Vertx vertx;
        private final DSLContext dsl;
        private final S sqlClient;
        private final PQ preparedQuery;
        private final RC resultCollector;
        private final SQLErrorConverter errorConverter;
        private final DataTypeMapperRegistry typeMapperRegistry;

        protected SQLEI(Vertx vertx, DSLContext dsl, S sqlClient, PQ preparedQuery, RC resultCollector,
                        SQLErrorConverter errorConverter, DataTypeMapperRegistry typeMapperRegistry) {
            this.vertx              = vertx;
            this.dsl                = dsl;
            this.sqlClient          = sqlClient;
            this.preparedQuery      = Optional.ofNullable(preparedQuery).orElseGet(this::defPrepareQuery);
            this.resultCollector    = Optional.ofNullable(resultCollector).orElseGet(this::defResultCollector);
            this.errorConverter     = Optional.ofNullable(errorConverter).orElseGet(this::defErrorConverter);
            this.typeMapperRegistry = Optional.ofNullable(typeMapperRegistry).orElseGet(this::defMapperRegistry);
        }

        protected final RuntimeException transientConnFailed(String errorMsg, Throwable cause) {
            final String sqlState = SQLStateClass.C08_CONNECTION_EXCEPTION.className();
            return this.errorConverter().handle(new SQLTransientConnectionException(errorMsg, sqlState, cause));
        }

        protected final RuntimeException nonTransientConnFailed(String errorMsg) {
            final String sqlState = SQLStateClass.C08_CONNECTION_EXCEPTION.className();
            return this.errorConverter().handle(new SQLNonTransientConnectionException(errorMsg, sqlState));
        }

        @NotNull
        protected abstract PQ defPrepareQuery();

        @NotNull
        protected abstract RC defResultCollector();

        protected @NotNull SQLErrorConverter defErrorConverter() {
            return SQLErrorConverter.DEFAULT;
        }

        protected @NotNull DataTypeMapperRegistry defMapperRegistry() {
            return new DataTypeMapperRegistry();
        }

        @Override
        public @NotNull Vertx vertx() { return vertx; }

        @Override
        public @NotNull DSLContext dsl() { return dsl; }

        @Override
        public @NotNull S sqlClient() { return sqlClient; }

        @Override
        public @NotNull PQ preparedQuery() { return preparedQuery; }

        @Override
        public @NotNull RC resultCollector() { return resultCollector; }

        @Override
        public @NotNull SQLErrorConverter errorConverter() { return errorConverter; }

        @Override
        public @NotNull DataTypeMapperRegistry typeMapperRegistry() { return typeMapperRegistry; }

        /**
         * To spawn executor in Database transaction
         *
         * @param sqlClient SQL client
         * @return new instance of executor
         */
        protected abstract SQLExecutor<S, B, PQ, RS, RC> withSqlClient(S sqlClient);

    }


    abstract static class SQLPQ<T> implements SQLPreparedQuery<T> {

        private static final Pattern NAMED_PARAM_PATTERN = Pattern.compile("(?<!:):(?!:)");
        private static final Logger LOGGER = LoggerFactory.getLogger(SQLPreparedQuery.class);

        @Override
        public final @NotNull String sql(@NotNull Configuration configuration, @NotNull Query query) {
            if (!query.isExecutable()) {
                throw new IllegalArgumentException("Query is not executable: " + query.getSQL());
            }
            final ParamType paramType = configuration.settings().getParamType();
            if (LOGGER.isTraceEnabled()) {
                LOGGER.debug("DEFAULT:             {}", query.getSQL());
                LOGGER.debug("NAMED:               {}", query.getSQL(ParamType.NAMED));
                LOGGER.debug("INLINED:             {}", query.getSQL(ParamType.INLINED));
                LOGGER.debug("NAMED_OR_INLINED:    {}", query.getSQL(ParamType.NAMED_OR_INLINED));
                LOGGER.debug("INDEXED:             {}", query.getSQL(ParamType.INDEXED));
                LOGGER.debug("FORCE_INDEXED:       {}", query.getSQL(ParamType.FORCE_INDEXED));
            }
            if (SQLDialect.POSTGRES.supports(configuration.dialect()) && paramType == ParamType.NAMED) {
                final String sql = NAMED_PARAM_PATTERN.matcher(query.getSQL(paramType)).replaceAll("\\$");
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("POSTGRESQL:          {}", sql);
                }
                return sql;
            }
            final String sql = query.getSQL(paramType == null ? ParamType.INDEXED : paramType);
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Prepared Query:          {}", sql);
            }
            return sql;
        }

        public final @NotNull T bindValues(@NotNull Query query, @NotNull DataTypeMapperRegistry mapperRegistry) {
            return this.convert(query.getParams(), mapperRegistry);
        }

        public final @NotNull List<T> bindValues(@NotNull Query query, @NotNull BindBatchValues bindBatchValues,
                                                 @NotNull DataTypeMapperRegistry mapperRegistry) {
            return this.convert(query.getParams(), bindBatchValues, mapperRegistry);
        }

        protected abstract T doConvert(Map<String, Param<?>> params, DataTypeMapperRegistry registry,
                                       BiFunction<String, Param<?>, ?> queryValue);

        private T convert(@NotNull Map<String, Param<?>> params, @NotNull DataTypeMapperRegistry registry) {
            return doConvert(params, registry, (k, v) -> v.getValue());
        }

        private List<T> convert(@NotNull Map<String, Param<?>> params, @NotNull BindBatchValues bindBatchValues,
                                @NotNull DataTypeMapperRegistry registry) {
            final List<String> fields = bindBatchValues.getMappingFields();
            final List<Object> values = bindBatchValues.getMappingValues();
            return bindBatchValues.getRecords()
                                  .stream()
                                  .map(record -> doConvert(params, registry, (paramNameOrIndex, param) -> {
                                      if (param.getParamName() != null) {
                                          return record.get(param.getParamName());
                                      }
                                      try {
                                          final int index = Integer.parseInt(paramNameOrIndex) - 1;
                                          return Optional.ofNullable(
                                                             (Object) record.get(record.field(fields.get(index))))
                                                         .orElseGet(() -> values.get(index));
                                      } catch (NumberFormatException e) {
                                          return record.get(paramNameOrIndex);
                                      }
                                  }))
                                  .collect(Collectors.toList());
        }

    }


    @SuppressWarnings("unchecked")
    abstract static class SQLExecutorBuilderImpl<S, B, PQ extends SQLPreparedQuery<B>, RS,
                                                        RC extends SQLResultCollector<RS>,
                                                        E extends SQLExecutorBuilder<S, B, PQ, RS, RC, E>>
        implements SQLExecutorBuilder<S, B, PQ, RS, RC, E> {

        private Vertx vertx;
        private DSLContext dsl;
        private S sqlClient;
        private PQ preparedQuery;
        private RC resultCollector;
        private SQLErrorConverter errorConverter;
        private DataTypeMapperRegistry typeMapperRegistry;

        public @NotNull Vertx vertx() { return Objects.requireNonNull(vertx, "Required Vert.x instance"); }

        public @NotNull E setVertx(Vertx vertx) {
            this.vertx = vertx;
            return (E) this;
        }

        public @NotNull DSLContext dsl() { return Objects.requireNonNull(dsl, "Required DSLContext instance"); }

        public @NotNull E setDSL(DSLContext dsl) {
            this.dsl = dsl;
            return (E) this;
        }

        public @NotNull S sqlClient() { return Objects.requireNonNull(sqlClient, "Required SQL client instance"); }

        public @NotNull E setSqlClient(S sqlClient) {
            this.sqlClient = sqlClient;
            return (E) this;
        }

        public PQ preparedQuery() { return preparedQuery; }

        public @NotNull E setPreparedQuery(PQ preparedQuery) {
            this.preparedQuery = preparedQuery;
            return (E) this;
        }

        public RC resultCollector() { return resultCollector; }

        public @NotNull E setResultCollector(RC resultCollector) {
            this.resultCollector = resultCollector;
            return (E) this;
        }

        public SQLErrorConverter errorConverter() { return errorConverter; }

        public @NotNull E setErrorConverter(SQLErrorConverter errorConverter) {
            this.errorConverter = errorConverter;
            return (E) this;
        }

        public DataTypeMapperRegistry typeMapperRegistry() { return typeMapperRegistry; }

        public @NotNull E setTypeMapperRegistry(DataTypeMapperRegistry typeMapperRegistry) {
            this.typeMapperRegistry = typeMapperRegistry;
            return (E) this;
        }

    }

}