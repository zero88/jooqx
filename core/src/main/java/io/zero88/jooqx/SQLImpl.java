package io.zero88.jooqx;

import java.sql.SQLNonTransientConnectionException;
import java.sql.SQLTransientConnectionException;
import java.util.List;
import java.util.Map;
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

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.With;
import lombok.experimental.Accessors;

final class SQLImpl {

    @Getter
    @Accessors(fluent = true)
    abstract static class SQLEI<S, B, PQ extends SQLPreparedQuery<B>, RS, RC extends SQLResultCollector<RS>>
        implements SQLExecutor<S, B, PQ, RS, RC> {

        private final Vertx vertx;
        private final DSLContext dsl;
        @With(AccessLevel.PROTECTED)
        private final S sqlClient;
        private final PQ preparedQuery;
        private final RC resultCollector;
        private final SQLErrorConverter errorConverter;
        private final DataTypeMapperRegistry typeMapperRegistry;

        protected SQLEI(Vertx vertx, DSLContext dsl, S sqlClient, PQ preparedQuery, RC resultCollector,
                        SQLErrorConverter errorConverter, DataTypeMapperRegistry typeMapperRegistry) {
            this.vertx = vertx;
            this.dsl = dsl;
            this.sqlClient = sqlClient;
            this.preparedQuery = Optional.ofNullable(preparedQuery).orElseGet(this::defPrepareQuery);
            this.resultCollector = Optional.ofNullable(resultCollector).orElseGet(this::defResultCollector);
            this.errorConverter = Optional.ofNullable(errorConverter).orElseGet(this::defErrorConverter);
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

        @NonNull
        protected abstract PQ defPrepareQuery();

        @NonNull
        protected abstract RC defResultCollector();

        @NonNull
        protected SQLErrorConverter defErrorConverter() {
            return SQLErrorConverter.DEFAULT;
        }

        @NonNull
        protected DataTypeMapperRegistry defMapperRegistry() {
            return new DataTypeMapperRegistry();
        }

    }


    abstract static class SQLPQ<T> implements SQLPreparedQuery<T> {

        private static final Pattern NAMED_PARAM_PATTERN = Pattern.compile("(?<!:):(?!:)");
        private static final Logger LOGGER = LoggerFactory.getLogger(SQLPreparedQuery.class);

        @Override
        public final @NonNull String sql(@NonNull Configuration configuration, @NonNull Query query) {
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

        public final @NotNull T bindValues(@NonNull Query query, @NonNull DataTypeMapperRegistry mapperRegistry) {
            return this.convert(query.getParams(), mapperRegistry);
        }

        public final @NotNull List<T> bindValues(@NonNull Query query, @NonNull BindBatchValues bindBatchValues,
                                                 @NonNull DataTypeMapperRegistry mapperRegistry) {
            return this.convert(query.getParams(), bindBatchValues, mapperRegistry);
        }

        protected abstract T doConvert(Map<String, Param<?>> params, DataTypeMapperRegistry registry,
                                       BiFunction<String, Param<?>, ?> queryValue);

        private T convert(@NonNull Map<String, Param<?>> params, @NonNull DataTypeMapperRegistry registry) {
            return doConvert(params, registry, (k, v) -> v.getValue());
        }

        private List<T> convert(@NonNull Map<String, Param<?>> params, @NonNull BindBatchValues bindBatchValues,
                                @NonNull DataTypeMapperRegistry registry) {
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

}
