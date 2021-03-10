package io.github.zero88.jooq.vertx.converter;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.jooq.Configuration;
import org.jooq.Converter;
import org.jooq.Param;
import org.jooq.Query;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.zero88.jooq.vertx.BindBatchValues;
import io.github.zero88.jooq.vertx.converter.ext.PgConverter;
import io.vertx.core.buffer.Buffer;

import lombok.NonNull;

abstract class SQLPreparedQueryImpl<T> implements SQLPreparedQuery<T> {

    private static final Pattern NAMED_PARAM_PATTERN = Pattern.compile("(?<!:):(?!:)");
    private static final Logger LOGGER = LoggerFactory.getLogger(SQLPreparedQuery.class);

    @Override
    public @NonNull String sql(@NonNull Configuration configuration, @NonNull Query query) {
        return sql(configuration, query, null);
    }

    public T bindValues(@NonNull Query query) {
        return this.convert(query.getParams());
    }

    public List<T> bindValues(@NonNull Query query, @NonNull BindBatchValues bindBatchValues) {
        return this.convert(query.getParams(), bindBatchValues);
    }

    protected abstract T doConvert(Map<String, Param<?>> params, BiFunction<String, Param<?>, ?> queryValue);

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

    @SuppressWarnings( {"unchecked", "rawtypes"})
    protected final Object convertToDatabaseType(String paramName, Param<?> param,
                                                 BiFunction<String, Param<?>, ?> queryValue) {
        /*
         * https://github.com/reactiverse/reactive-pg-client/issues/191 enum types are treated as unknown
         * DataTypes. Workaround is to convert them to string before adding to the Tuple.
         */
        final Object val = queryValue.apply(paramName, param);
        if (Enum.class.isAssignableFrom(param.getBinding().converter().toType())) {
            if (val == null) {
                return null;
            }
            return val.toString();
        }
        // jooq treats BINARY types as byte[] but the reactive client expects a Buffer to write to blobs
        if (byte[].class.isAssignableFrom(param.getBinding().converter().fromType())) {
            byte[] bytes = ((Converter<byte[], Object>) param.getBinding().converter()).to(val);
            if (bytes == null) {
                return null;
            }
            return Buffer.buffer(bytes);
        }
        if (param.getBinding().converter() instanceof PgConverter) {
            return ((PgConverter) param.getBinding().converter()).rowConverter().to(val);
        }
        return ((Converter<Object, Object>) param.getBinding().converter()).to(val);
    }

    private T convert(@NonNull Map<String, Param<?>> params) {
        return doConvert(params, (k, v) -> v.getValue());
    }

    private List<T> convert(@NonNull Map<String, Param<?>> params, @NonNull BindBatchValues bindBatchValues) {
        final List<String> fields = bindBatchValues.getMappingFields();
        final List<Object> values = bindBatchValues.getMappingValues();
        return bindBatchValues.getRecords().stream().map(record -> doConvert(params, (paramNameOrIndex, param) -> {
            if (param.getParamName() != null) {
                return record.get(param.getParamName());
            }
            try {
                final int index = Integer.parseInt(paramNameOrIndex) - 1;
                return Optional.ofNullable((Object) record.get(record.field(fields.get(index))))
                               .orElseGet(() -> values.get(index));
            } catch (NumberFormatException e) {
                return record.get(paramNameOrIndex);
            }
        })).collect(Collectors.toList());
    }

}
