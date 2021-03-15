package io.zero88.jooqx;

import java.sql.SQLNonTransientConnectionException;
import java.sql.SQLTransientConnectionException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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
import io.zero88.jooqx.adapter.SelectStrategy;
import io.zero88.jooqx.datatype.DataTypeMapperRegistry;

import lombok.AccessLevel;
import lombok.Builder.Default;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.With;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

final class SQLImpl {

    @Getter
    @SuperBuilder
    @Accessors(fluent = true)
    abstract static class SQLEI<S, P, RS, C extends SQLResultCollector<RS>> implements SQLExecutor<S, P, RS, C> {

        private final Vertx vertx;
        private final DSLContext dsl;
        @With(AccessLevel.PROTECTED)
        private final S sqlClient;
        @Default
        private final SQLErrorConverter errorConverter = SQLErrorConverter.DEFAULT;
        @Default
        private final DataTypeMapperRegistry typeMapperRegistry = new DataTypeMapperRegistry();

        protected final RuntimeException connFailed(String errorMsg, Throwable cause) {
            return errorConverter().handle(
                new SQLTransientConnectionException(errorMsg, SQLStateClass.C08_CONNECTION_EXCEPTION.className(),
                                                    cause));
        }

        protected final RuntimeException connFailed(String errorMsg) {
            return errorConverter().handle(
                new SQLNonTransientConnectionException(errorMsg, SQLStateClass.C08_CONNECTION_EXCEPTION.className()));
        }

    }


    abstract static class SQLPQ<T> implements SQLPreparedQuery<T> {

        private static final Pattern NAMED_PARAM_PATTERN = Pattern.compile("(?<!:):(?!:)");
        private static final Logger LOGGER = LoggerFactory.getLogger(SQLPreparedQuery.class);

        @Override
        public @NonNull String sql(@NonNull Configuration configuration, @NonNull Query query) {
            return sql(configuration, query, null);
        }

        public T bindValues(@NonNull Query query, @NonNull DataTypeMapperRegistry mapperRegistry) {
            return this.convert(query.getParams(), mapperRegistry);
        }

        public List<T> bindValues(@NonNull Query query, @NonNull BindBatchValues bindBatchValues,
                                  @NonNull DataTypeMapperRegistry mapperRegistry) {
            return this.convert(query.getParams(), bindBatchValues, mapperRegistry);
        }

        protected abstract T doConvert(Map<String, Param<?>> params, DataTypeMapperRegistry registry,
                                       BiFunction<String, Param<?>, ?> queryValue);

        protected final String sql(@NonNull Configuration configuration, @NonNull Query query, ParamType paramType) {
            if (!query.isExecutable()) {
                throw new IllegalArgumentException("Query is not executable: " + query.getSQL());
            }
            if (LOGGER.isTraceEnabled()) {
                LOGGER.debug("DEFAULT:             {}", query.getSQL());
                LOGGER.debug("NAMED:               {}", query.getSQL(ParamType.NAMED));
                LOGGER.debug("INLINED:             {}", query.getSQL(ParamType.INLINED));
                LOGGER.debug("NAMED_OR_INLINED:    {}", query.getSQL(ParamType.NAMED_OR_INLINED));
                LOGGER.debug("INDEXED:             {}", query.getSQL(ParamType.INDEXED));
                LOGGER.debug("FORCE_INDEXED:       {}", query.getSQL(ParamType.FORCE_INDEXED));
            }
            if (SQLDialect.POSTGRES.supports(configuration.dialect()) && paramType == null) {
                final String sql = NAMED_PARAM_PATTERN.matcher(query.getSQL(ParamType.NAMED)).replaceAll("\\$");
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


    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    abstract static class SQLRC<RS> implements SQLResultCollector<RS> {

        protected void warnManyResult(boolean check, @NonNull SelectStrategy strategy) {
            if (check) {
                LOGGER.warn("Query strategy is [{}] but query result contains more than one row", strategy);
            }
        }

    }

}
